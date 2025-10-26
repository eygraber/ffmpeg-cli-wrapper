package net.bramp.ffmpeg.kotlin.progress

import java.io.Closeable
import java.io.IOException
import java.net.URI

/** Parses the FFmpeg progress fields  */
interface ProgressParser : Closeable {

  /**
   * The URL to parse to FFmpeg to communicate with this parser
   *
   * @return The URI to communicate with FFmpeg.
   */
  val uri: URI

  @Throws(IOException::class)
  fun start()

  @Throws(IOException::class)
  fun stop()
}
