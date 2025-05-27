package net.bramp.ffmpeg.options

import org.apache.commons.lang3.math.Fraction
import java.beans.ConstructorProperties

/**
 * Encoding options for video
 *
 * @author bramp
 */
data class VideoEncodingOptions @ConstructorProperties(
  "enabled",
  "codec",
  "frame_rate",
  "width",
  "height",
  "bit_rate",
  "frames",
  "video_filter",
  "preset",
) constructor(
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
