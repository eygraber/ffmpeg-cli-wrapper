package net.bramp.ffmpeg.progress

import com.google.common.base.Charsets
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class StreamProgressParser(private val listener: ProgressListener) {

  @Throws(IOException::class)
  fun processStream(stream: InputStream) {
    processReader(InputStreamReader(stream, Charsets.UTF_8))
  }

  @Throws(IOException::class)
  fun processReader(reader: Reader) {
    val `in` = wrapInBufferedReader(reader)
    var line: String?
    var p = Progress()
    while(`in`.readLine().also { line = it } != null) {
      if (p.parseLine(line!!)) {
        listener.progress(p)
        p = Progress()
      }
    }
  }

  companion object {
    private fun wrapInBufferedReader(reader: Reader): BufferedReader = if(reader is BufferedReader) {
      reader
    }
    else {
      BufferedReader(reader)
    }
  }
}
