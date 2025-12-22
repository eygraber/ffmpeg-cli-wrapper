package net.bramp.ffmpeg.kotlin.nut

import org.apache.commons.lang3.math.Fraction
import java.io.EOFException
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.TreeMap

/** A video or audio frame  */
class Frame {
  lateinit var stream: Stream
  var flags: Long = 0
  var pts: Long = 0
  lateinit var data: ByteArray
  var sideData: Map<String, Any>? = null
  var metaData: Map<String, Any>? = null

  @Throws(IOException::class)
  private fun readMetaData(input: NutDataInputStream): Map<String, Any> {
    val data: MutableMap<String, Any> = TreeMap()
    val count = input.readVarLong()
    repeat(count.toInt()) {
      val name = String(input.readVarArray(), StandardCharsets.UTF_8)
      val type = input.readSignedVarInt()
      val value: Any
      if(type == -1L) {
        value = String(input.readVarArray(), StandardCharsets.UTF_8)
      }
      else if(type == -2L) {
        val k = String(input.readVarArray(), StandardCharsets.UTF_8)
        val v = String(input.readVarArray(), StandardCharsets.UTF_8)
        value = "$k=$v" // TODO Change this some how
      }
      else if(type == -3L) {
        value = input.readSignedVarInt()
      }
      else if(type == -4L) {
        /*
         * t (v coded universal timestamp) tmp v id= tmp % time_base_count value= (tmp /
         * time_base_count) * timeBase[id]
         */
        value = input.readVarLong() // TODO Convert to timestamp
      }
      else if(type < -4) {
        val denominator = -type - 4
        val numerator = input.readSignedVarInt()
        value = Fraction.getFraction(numerator.toInt(), denominator.toInt())
      }
      else {
        value = type
      }
      data[name] = value
    }
    return data
  }

  @Suppress("ThrowsCount")
  @Throws(IOException::class)
  fun read(nut: NutReader, input: NutDataInputStream, code: Int) {
    if(code == 'N'.code) {
      throw IOException("Illegal frame code: $code")
    }
    val fc = nut.header.frameCodes[code]
    flags = fc.flags
    if(flags and FLAG_INVALID == FLAG_INVALID) {
      throw IOException("Using invalid framecode: $code")
    }
    if(flags and FLAG_CODED == FLAG_CODED) {
      val codedFlags = input.readVarLong()
      flags = flags xor codedFlags
    }
    var size = fc.dataSizeLsb
    val streamId: Int
    val codedPts: Long
    var headerIdx = fc.headerIdx
    var frameRes = fc.reservedCount
    if(flags and FLAG_STREAM_ID == FLAG_STREAM_ID) {
      streamId = input.readVarInt()
      if(streamId >= nut.streams.size) {
        throw IOException(
          "Illegal stream id value $streamId must be < " + nut.streams.size,
        )
      }
    }
    else {
      streamId = fc.streamId
    }
    stream = nut.streams[streamId]
    if(flags and FLAG_CODED_PTS == FLAG_CODED_PTS) {
      codedPts = input.readVarLong()
      if(codedPts < 1 shl stream.header.msbPtsShift) {
        val mask = (1L shl stream.header.msbPtsShift) - 1
        val delta = stream.lastPts - mask / 2
        pts = (codedPts - delta and mask) + delta
      }
      else {
        pts = codedPts - (1L shl stream.header.msbPtsShift)
      }
    }
    else {
      // TODO Test this code path
      pts = stream.lastPts + fc.ptsDelta
    }
    stream.lastPts = pts
    if(flags and FLAG_SIZE_MSB == FLAG_SIZE_MSB) {
      val dataSizeMsb = input.readVarInt()
      size += fc.dataSizeMul * dataSizeMsb
    }
    if(flags and FLAG_MATCH_TIME == FLAG_MATCH_TIME) {
      fc.matchTimeDelta = input.readSignedVarInt()
    }
    if(flags and FLAG_HEADER_IDX == FLAG_HEADER_IDX) {
      headerIdx = input.readVarInt()
      if(headerIdx >= nut.header.elision.size) {
        throw IOException(
          "Illegal header index $headerIdx must be < " + nut.header.elision.size,
        )
      }
    }
    if(flags and FLAG_RESERVED == FLAG_RESERVED) {
      frameRes = input.readVarInt()
    }
    repeat(frameRes) {
      input.readVarLong() // Discard
    }
    if(flags and FLAG_CHECKSUM == FLAG_CHECKSUM) {
      input.readInt()
      // TODO Test checksum
    }
    if(size > 4096) {
      headerIdx = 0
    }

    // Now data
    if(flags and FLAG_SM_DATA == FLAG_SM_DATA) {
      // TODO Test this path.
      if(nut.header.version < 4) {
        throw IOException("Frame SM Data not allowed in version 4 or less")
      }
      val pos = input.offset()
      sideData = readMetaData(input)
      metaData = readMetaData(input)
      val metadataLen = input.offset() - pos
      if(metadataLen > size) {
        throw EOFException()
      }
      size -= metadataLen.toInt()
    }
    else {
      sideData = null
      metaData = null
    }

    // TODO Use some kind of byte pool
    data = ByteArray(size)
    val elision = nut.header.elision[headerIdx]
    System.arraycopy(elision, 0, data, 0, elision.size)
    input.readFully(b = data, off = elision.size, len = size - elision.size)
  }

  override fun toString(): String =
    "Frame(id=${stream.header.id}, pts=$pts, data=(${data.size} bytes))"

  companion object {
    // TODO Change this to a enum
    const val FLAG_KEY = 1L shl 0
    const val FLAG_EOR = 1L shl 1
    const val FLAG_CODED_PTS = 1L shl 3
    const val FLAG_STREAM_ID = 1L shl 4
    const val FLAG_SIZE_MSB = 1L shl 5
    const val FLAG_CHECKSUM = 1L shl 6
    const val FLAG_RESERVED = 1L shl 7
    const val FLAG_SM_DATA = 1L shl 8
    const val FLAG_HEADER_IDX = 1L shl 10
    const val FLAG_MATCH_TIME = 1L shl 11
    const val FLAG_CODED = 1L shl 12
    const val FLAG_INVALID = 1L shl 13
  }
}
