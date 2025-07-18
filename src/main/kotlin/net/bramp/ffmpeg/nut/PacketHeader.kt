package net.bramp.ffmpeg.nut

import com.google.common.base.MoreObjects
import java.io.IOException
import java.util.Locale

class PacketHeader {
  var startcode: Long = 0
  var forwardPtr: Long = 0
  var checksum = 0 // header checksum
  var end: Long = 0 // End byte of packet

  @Throws(IOException::class)
  fun read(input: NutDataInputStream, startcode: Long) {
    this.startcode = startcode
    forwardPtr = input.readVarLong()
    if(forwardPtr > 4096) {
      val expected = input.getCRC()
      checksum = input.readInt()
      if(checksum.toLong() != expected) {
        // TODO This code path has never been tested.
        throw IOException(
          String.format(Locale.ROOT, "invalid header checksum %X want %X", expected, checksum),
        )
      }
    }
    input.resetCRC()
    end = input.offset() + forwardPtr - 4 // 4 bytes for footer CRC
  }

  override fun toString(): String {
    var helper = MoreObjects.toStringHelper(this)
      .add("startcode", Packet.StartCode.toString(startcode))
      .add("forwardPtr", forwardPtr)
    if(forwardPtr > 4096) {
      helper = helper.add("checksum", checksum)
    }
    return helper.toString()
  }
}
