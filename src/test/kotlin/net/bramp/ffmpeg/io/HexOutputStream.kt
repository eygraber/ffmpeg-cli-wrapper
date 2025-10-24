package net.bramp.ffmpeg.io

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.StandardCharsets

/** Converts bytes into hex output */
class HexOutputStream(out: OutputStream) : OutputStream() {
  private val writer: Writer = OutputStreamWriter(out, StandardCharsets.UTF_8)
  private var count = 0

  override fun write(b: Int) {
    writer.write("%02X ".format(b and 0xFF))
    count++
    if(count > 16) {
      count = 0
      writer.write("\n")
    }
  }

  override fun write(b: ByteArray, off: Int, len: Int) {
    for(i in 0 until len) {
      write(b[off + i].toInt())
    }
  }

  override fun write(b: ByteArray) {
    write(b, 0, b.size)
  }

  override fun flush() {
    writer.flush()
  }

  override fun close() {
    writer.close()
  }
}
