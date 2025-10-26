package net.bramp.ffmpeg.kotlin.nut

interface NutReaderListener {
  /**
   * Executes when a new stream is found.
   *
   * @param stream The stream
   */
  fun stream(stream: Stream)

  /**
   * Executes when a new frame is found.
   *
   * @param frame A single Frame
   */
  fun frame(frame: Frame)
}
