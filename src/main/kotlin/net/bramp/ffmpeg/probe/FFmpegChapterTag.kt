package net.bramp.ffmpeg.probe

import kotlinx.serialization.Serializable

@Serializable
data class FFmpegChapterTag(
  val title: String? = null,
)
