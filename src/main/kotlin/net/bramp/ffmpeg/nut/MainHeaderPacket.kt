package net.bramp.ffmpeg.nut

import com.google.common.base.MoreObjects
import org.apache.commons.lang3.math.Fraction
import java.io.IOException
import java.util.Locale

class MainHeaderPacket : Packet() {
  var version: Long = 0
  var minorVersion: Long = 0
  var streamCount: Int = 0
  var maxDistance: Long = 0
  lateinit var timeBase: Array<Fraction>
  var flags: Long = 0

  val frameCodes: MutableList<FrameCode> = ArrayList()
  val elision: MutableList<ByteArray> = ArrayList()

  @Throws(IOException::class)
  override fun readBody(dataInputStream: NutDataInputStream) {
    frameCodes.clear()

    version = dataInputStream.readVarLong()
    if(version > 3) {
      minorVersion = dataInputStream.readVarLong()
    }

    streamCount = dataInputStream.readVarInt()
    if(streamCount >= 250) {
      throw IOException("Illegal stream count $streamCount must be < 250")
    }

    maxDistance = dataInputStream.readVarLong()
    if(maxDistance > 65_536) {
      maxDistance = 65_536
    }

    val timeBaseCount = dataInputStream.readVarInt()
    timeBase = Array(timeBaseCount) {
      val timeBaseNum = dataInputStream.readVarLong().toInt()
      val timeBaseDenom = dataInputStream.readVarLong().toInt()
      Fraction.getFraction(timeBaseNum, timeBaseDenom)
    }

    var pts: Long = 0
    var mul = 1
    var streamId = 0
    var headerIdx = 0

    var match = 1L - (1L shl 62)
    var size: Int
    var reserved: Int
    var count: Long

    var i = 0
    while(i < 256) {
      val flags = dataInputStream.readVarLong()
      val fields = dataInputStream.readVarLong()
      if(fields > 0) {
        pts = dataInputStream.readSignedVarInt()
      }
      if(fields > 1) {
        mul = dataInputStream.readVarInt()
        if(mul >= 16_384) {
          throw IOException("Illegal mul value $mul must be < 16384")
        }
      }
      if(fields > 2) {
        streamId = dataInputStream.readVarInt()
        if(streamId >= streamCount) {
          throw IOException(
            "Illegal stream id value $streamId must be < $streamCount",
          )
        }
      }
      size = if(fields > 3) {
        dataInputStream.readVarInt()
      }
      else {
        0
      }
      if(fields > 4) {
        reserved = dataInputStream.readVarInt()
        if(reserved >= 256) {
          throw IOException("Illegal reserved frame count $reserved must be < 256")
        }
      }
      else {
        reserved = 0
      }
      count = if(fields > 5) {
        dataInputStream.readVarLong()
      }
      else {
        mul.toLong() - size
      }
      if(fields > 6) {
        match = dataInputStream.readSignedVarInt()
      }
      if(fields > 7) {
        headerIdx = dataInputStream.readVarInt()
      }
      repeat(fields.toInt() - 8) {
        dataInputStream.readVarLong() // Throw away
      }

      if(streamId >= streamCount) {
        throw IOException(
          String.format(Locale.ROOT, "Invalid stream value %d, must be < %d", streamId, streamCount),
        )
      }

      if(count <= 0 || count > 256 - i - (if(i <= 'N'.code) 1 else 0)) {
        throw IOException(
          String.format(
            Locale.ROOT,
            "Invalid count value %d, must be > 0 && < %d",
            count,
            256 - i - if(i <= 'N'.code) 1 else 0,
          ),
        )
      }

      var j = 0
      while(j < count && i < 256) {
        val fc = FrameCode()
        frameCodes.add(fc)

        // Skip 'N' because that is an illegal frame code
        if(i == 'N'.code) {
          fc.flags = Frame.FLAG_INVALID
          j--
          j++
          i++
          continue
        }

        fc.flags = flags
        fc.streamId = streamId
        fc.dataSizeMul = mul
        fc.dataSizeLsb = size + j
        fc.ptsDelta = pts
        fc.reservedCount = reserved
        fc.matchTimeDelta = match
        fc.headerIdx = headerIdx

        if(fc.dataSizeLsb >= 16_384) {
          throw IOException("Illegal dataSizeLsb value " + fc.dataSizeLsb + " must be < 16384")
        }
        j++
        i++
      }
    }

    var remain = 1024
    if(dataInputStream.offset() < header.end - 4) {
      val headerCount = dataInputStream.readVarInt()
      if(headerCount >= 128) {
        throw IOException("Invalid header_count value $headerCount must be < 128")
      }

      elision.clear()
      elision.add(ByteArray(0)) // First elision is always empty
      repeat(headerCount) {
        val e = dataInputStream.readVarArray()
        if(e.isEmpty() || e.size >= 256) {
          throw IOException("Invalid elision length " + e.size + " must be > 0 and < 256")
        }
        if(e.size > remain) {
          throw IOException(
            "Invalid elision length value " + e.size + " must be <= " + remain,
          )
        }
        remain -= e.size
        elision.add(e)
      }
    }

    if(version > 3 && dataInputStream.offset() < header.end - 4) {
      flags = dataInputStream.readVarLong()
    }
  }

  override fun toString(): String = MoreObjects.toStringHelper(this)
    .add("header", header)
    .add("version", version)
    .add("minorVersion", minorVersion)
    .add("streamCount", streamCount)
    .add("maxDistance", maxDistance)
    .add("timeBase", timeBase)
    .add("flags", flags)
    .add("frameCodes", frameCodes.size)
    .add("elision", elision)
    .add("footer", footer)
    .toString()

  companion object {
    const val BROADCAST_MODE: Int = 0
  }
}
