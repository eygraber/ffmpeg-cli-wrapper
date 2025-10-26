package net.bramp.ffmpeg.kotlin.nut

import java.io.IOException

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
          "invalid header checksum ${expected.toString(16).uppercase()} want ${checksum.toString(16).uppercase()}",
        )
      }
    }
    input.resetCRC()
    end = input.offset() + forwardPtr - 4 // 4 bytes for footer CRC
  }

  override fun toString(): String {
    val checksumStr = if(forwardPtr > 4096) ", checksum=$checksum" else ""
    return "PacketHeader(startcode=${Packet.StartCode.toString(startcode)}, forwardPtr=$forwardPtr$checksumStr)"
  }
}
