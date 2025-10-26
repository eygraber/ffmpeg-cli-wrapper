package net.bramp.ffmpeg.kotlin.probe

import kotlinx.serialization.Serializable

@Serializable
data class FFmpegError(
  val code: Int = 0,
  val string: String? = null,
)
