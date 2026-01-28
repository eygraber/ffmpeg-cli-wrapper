package net.bramp.ffmpeg.kotlin.io

import org.slf4j.Logger
import java.io.FilterReader
import java.io.IOException
import java.io.Reader

/**
 * Wraps a Reader, and logs full lines of input as it is read.
 *
 * @author bramp
 */
class LoggingFilterReader(input: Reader?, val logger: Logger) : FilterReader(input) {
  val buffer = StringBuilder()

  private fun log() {
    if(buffer.isNotEmpty()) {
      // TODO Change from debug, to a user defined level
      logger.debug(buffer.toString())
      buffer.setLength(0)
    }
  }

  @Throws(IOException::class)
  override fun read(cbuf: CharArray, off: Int, len: Int): Int {
    val ret = super.read(cbuf, off, len)
    if(ret != -1) {
      buffer.append(cbuf, off, ret)
    }

    // If end of stream, or contains new line
    if(ret == -1 || indexOf(array = cbuf, c = LOG_CHAR, off = off, len = ret) != -1) {
      // BUG this will log a unfinished line, if a string such as
      // "line \n unfinished" is read.
      log()
    }
    return ret
  }

  @Throws(IOException::class)
  override fun read(): Int {
    val ret = super.read()
    if(ret != -1) {
      buffer.append(ret.toChar())
    }

    // If end of stream, or contains new line
    if(ret == -1 || ret == LOG_CHAR.code) {
      log()
    }
    return ret
  }

  companion object {
    const val LOG_CHAR = '\n'
    private fun indexOf(array: CharArray, c: Char, off: Int, len: Int): Int {
      for(i in off until off + len) {
        if(array[i] == c) {
          return i
        }
      }
      return -1
    }
  }
}
