package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FFmpegChapter(
  var id: Long = 0,
  @SerialName("time_base") var timeBase: String? = null,
  var start: Long = 0,
  @SerialName("start_time") var startTime: String? = null,
  var end: Long = 0,
  @SerialName("end_time") var endTime: String? = null,
  var tags: FFmpegChapterTag? = null,
)
