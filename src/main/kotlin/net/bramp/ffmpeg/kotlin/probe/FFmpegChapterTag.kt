package net.bramp.ffmpeg.kotlin.probe

import kotlinx.serialization.Serializable

@Serializable
data class FFmpegChapterTag(
  val title: String? = null,
)
