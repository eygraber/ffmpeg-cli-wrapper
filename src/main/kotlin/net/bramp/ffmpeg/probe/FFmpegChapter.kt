package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FFmpegChapter(
  val id: Long = 0,
  @SerialName("time_base") val timeBase: String? = null,
  val start: Long = 0,
  @SerialName("start_time") val startTime: String? = null,
  val end: Long = 0,
  @SerialName("end_time") val endTime: String? = null,
  val tags: FFmpegChapterTag? = null,
)
