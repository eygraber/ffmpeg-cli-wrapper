package net.bramp.ffmpeg.options

/**
 * Encoding options that are specific to the main output.
 *
 * @author bramp
 */
data class MainEncodingOptions(
  val format: String?,
  val startOffset: Long?,
  val duration: Long?,
)
