package net.bramp.ffmpeg.kotlin.nut

import java.io.IOException

class PacketFooter {
  var checksum = 0

  @Throws(IOException::class)
  fun read(input: NutDataInputStream) {
    val expected = input.getCRC()
    checksum = input.readInt()
    if(checksum.toLong() != expected) {
      // throw new IOException(String.format("invalid packet checksum %X want %X", expected,
      // checksum));
      Packet.LOG.debug("invalid packet checksum {} want {}", expected, checksum)
    }
    input.resetCRC()
  }

  override fun toString(): String = "PacketFooter(checksum=$checksum)"
}
