package net.bramp.ffmpeg.options

import java.beans.ConstructorProperties

/**
 * Encoding options that are specific to the main output.
 *
 * @author bramp
 */
data class MainEncodingOptions @ConstructorProperties(
  "format",
  "startOffset",
  "duration",
) constructor(
  val format: String?,
  val startOffset: Long?,
  val duration: Long?,
)
