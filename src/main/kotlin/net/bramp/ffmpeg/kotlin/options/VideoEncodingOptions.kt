package net.bramp.ffmpeg.kotlin.options

import org.apache.commons.lang3.math.Fraction

/**
 * Encoding options for video
 *
 * @author bramp
 */
data class VideoEncodingOptions(
  val isEnabled: Boolean,
  val codec: String?,
  val frameRate: Fraction?,
  val width: Int,
  val height: Int,
  val bitRate: Long,
  val frames: Int?,
  val filter: String?,
  val preset: String?,
)
