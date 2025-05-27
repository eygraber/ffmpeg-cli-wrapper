package net.bramp.ffmpeg.options

import java.beans.ConstructorProperties

/**
 * Encoding options for audio
 *
 * @author bramp
 */
data class AudioEncodingOptions @ConstructorProperties(
  "enabled",
  "codec",
  "channels",
  "sample_rate",
  "sample_format",
  "bit_rate",
  "quality",
) constructor(
  val isEnabled: Boolean,
  val codec: String?,
  val channels: Int,
  val sampleRate: Int,
  val sampleFormat: String?,
  val bitRate: Long,
  val quality: Double?,
)
