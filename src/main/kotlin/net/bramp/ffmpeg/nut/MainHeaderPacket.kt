package net.bramp.ffmpeg.nut

import com.google.common.base.MoreObjects
import org.apache.commons.lang3.math.Fraction
import java.io.IOException

class MainHeaderPacket : Packet() {
  var version: Long = 0
  var minorVersion: Long = 0
  var streamCount: Int = 0
  var maxDistance: Long = 0
  lateinit var timeBase: Array<Fraction>
  var flags: Long = 0

  val frameCodes: MutableList<FrameCode> = ArrayList<FrameCode>()
  val elision: MutableList<ByteArray> = ArrayList<ByteArray>()

  @Throws(IOException::class)
  override fun readBody(`in`: NutDataInputStream) {
    frameCodes.clear()

    version = `in`.readVarLong()
    if(version > 3) {
      minorVersion = `in`.readVarLong()
    }

    streamCount = `in`.readVarInt()
    if(streamCount >= 250) {
      throw IOException("Illegal stream count " + streamCount + " must be < 250")
    }

    maxDistance = `in`.readVarLong()
    if(maxDistance > 65536) {
      maxDistance = 65536
    }

    val time_base_count = `in`.readVarInt()
    timeBase = Array(time_base_count) {
      val time_base_num = `in`.readVarLong().toInt()
      val time_base_denom = `in`.readVarLong().toInt()
      Fraction.getFraction(time_base_num, time_base_denom)
    }

    var pts: Long = 0
    var mul = 1
    var stream_id = 0
    var header_idx = 0

    var match = 1L - (1L shl 62)
    var size: Int
    var reserved: Int
    var count: Long

    var i = 0
    while(i < 256) {
      val flags = `in`.readVarLong()
      val fields = `in`.readVarLong()
      if (fields > 0) {
        pts = `in`.readSignedVarInt()
      }
      if (fields > 1) {
        mul = `in`.readVarInt()
        if (mul >= 16384) {
          throw IOException("Illegal mul value " + mul + " must be < 16384")
        }
      }
      if (fields > 2) {
        stream_id = `in`.readVarInt()
        if (stream_id >= streamCount) {
          throw IOException(
            "Illegal stream id value " + stream_id + " must be < " + streamCount,
          )
        }
      }
      if (fields > 3) {
        size = `in`.readVarInt()
      }
      else {
        size = 0
      }
      if (fields > 4) {
        reserved = `in`.readVarInt()
        if (reserved >= 256) {
          throw IOException("Illegal reserved frame count " + reserved + " must be < 256")
        }
      }
      else {
        reserved = 0
      }
      if (fields > 5) {
        count = `in`.readVarLong()
      }
      else {
        count = mul.toLong() - size
      }
      if (fields > 6) {
        match = `in`.readSignedVarInt()
      }
      if (fields > 7) {
        header_idx = `in`.readVarInt()
      }
      for (j in 8..<fields) {
        `in`.readVarLong() // Throw away
      }

      if (stream_id >= streamCount) {
        throw IOException(
          String.format("Invalid stream value %d, must be < %d", stream_id, streamCount),
        )
      }

      if (count <= 0 || (count > 256 - i - (if (i <= 'N'.code) 1 else 0))) {
        throw IOException(
          String.format(
            "Invalid count value %d, must be > 0 && < %d",
            count,
            256 - i - (if (i <= 'N'.code) 1 else 0),
          ),
        )
      }

      var j = 0
      while (j < count && i < 256) {
        val fc = FrameCode()
        frameCodes.add(fc)

        // Skip 'N' because that is an illegal frame code
        if (i == 'N'.code) {
          fc.flags = Frame.FLAG_INVALID
          j--
          j++
          i++
          continue
        }

        fc.flags = flags
        fc.streamId = stream_id
        fc.dataSizeMul = mul
        fc.dataSizeLsb = size + j
        fc.ptsDelta = pts
        fc.reservedCount = reserved
        fc.matchTimeDelta = match
        fc.headerIdx = header_idx

        if (fc.dataSizeLsb >= 16384) {
          throw IOException("Illegal dataSizeLsb value " + fc.dataSizeLsb + " must be < 16384")
        }
        j++
        i++
      }
    }

    var remain = 1024
    if(`in`.offset() < (header.end - 4)) {
      val header_count = `in`.readVarInt()
      if(header_count >= 128) {
        throw IOException("Invalid header_count value $header_count must be < 128")
      }

      elision.clear()
      elision.add(ByteArray(0)) // First elision is always empty
      for(i in 1..<header_count) {
        val e = `in`.readVarArray()
        if (e.isEmpty() || e.size >= 256) {
          throw IOException("Invalid elision length " + e.size + " must be > 0 and < 256")
        }
        if (e.size > remain) {
          throw IOException(
            "Invalid elision length value " + e.size + " must be <= " + remain,
          )
        }
        remain -= e.size
        elision.add(e)
      }
    }

    if(version > 3 && (`in`.offset() < (header.end - 4))) {
      flags = `in`.readVarLong()
    }
  }

  public override fun toString(): String = MoreObjects.toStringHelper(this)
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
