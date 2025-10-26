package net.bramp.ffmpeg.kotlin.options

/**
 * Encoding options for audio
 *
 * @author bramp
 */
data class AudioEncodingOptions(
  val isEnabled: Boolean,
  val codec: String?,
  val channels: Int,
  val sampleRate: Int,
  val sampleFormat: String?,
  val bitRate: Long,
  val quality: Double?,
)
