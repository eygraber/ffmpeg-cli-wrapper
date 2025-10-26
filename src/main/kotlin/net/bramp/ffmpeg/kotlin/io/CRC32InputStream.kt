package net.bramp.ffmpeg.kotlin.io

import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.CRC32

/**
 * Calculates the CRC32 for all bytes read through the input stream. Using the java.util.zip.CRC32
 * class to calculate the checksum.
 */
class CRC32InputStream(input: InputStream?) : FilterInputStream(input) {
  private val crc = CRC32()

  val value: Long
    get() = crc.value

  fun resetCrc() {
    crc.reset()
  }

  @Throws(IOException::class)
  override fun read(): Int {
    val b = `in`.read()
    if(b >= 0) {
      crc.update(b)
    }
    return b
  }

  @Throws(IOException::class)
  override fun read(b: ByteArray): Int {
    val len = `in`.read(b)
    crc.update(b, 0, len)
    return len
  }

  @Throws(IOException::class)
  override fun read(b: ByteArray, off: Int, len: Int): Int {
    val actual = `in`.read(b, off, len)
    crc.update(b, off, actual)
    return actual
  }

  @Throws(IOException::class)
  override fun skip(n: Long): Long {
    var i: Long = 0
    while(i < n) {
      read()
      i++
    }
    return i
  }

  @Synchronized
  override fun mark(readlimit: Int) = throw UnsupportedOperationException("mark not supported")

  @Synchronized
  @Throws(IOException::class)
  override fun reset() = throw UnsupportedOperationException("reset not supported")

  override fun markSupported(): Boolean = false
}
