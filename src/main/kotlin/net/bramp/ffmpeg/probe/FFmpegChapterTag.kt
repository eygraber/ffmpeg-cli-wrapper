package net.bramp.ffmpeg.probe

import kotlinx.serialization.Serializable

@Serializable
data class FFmpegChapterTag(
  var title: String? = null,
)
