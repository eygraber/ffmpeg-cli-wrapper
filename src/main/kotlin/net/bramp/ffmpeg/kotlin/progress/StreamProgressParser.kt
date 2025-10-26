package net.bramp.ffmpeg.kotlin.progress

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets

class StreamProgressParser(private val listener: ProgressListener) {

  @Throws(IOException::class)
  fun processStream(stream: InputStream) {
    processReader(InputStreamReader(stream, StandardCharsets.UTF_8))
  }

  @Throws(IOException::class)
  fun processReader(reader: Reader) {
    val bufferedReader = wrapInBufferedReader(reader)
    var p = Progress()
    bufferedReader.forEachLine { line ->
      val (newProgress, isFinished) = p.parseLine(line)
      if(newProgress != null) {
        p = newProgress
      }

      if(isFinished) {
        listener.progress(p)
        p = Progress()
      }
    }
  }

  companion object {
    private fun wrapInBufferedReader(reader: Reader): BufferedReader =
      reader as? BufferedReader ?: BufferedReader(reader)
  }
}
