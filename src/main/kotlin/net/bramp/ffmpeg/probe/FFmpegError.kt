package net.bramp.ffmpeg.probe

import kotlinx.serialization.Serializable

@Serializable
data class FFmpegError(
  var code: Int = 0,
  var string: String? = null,
)
