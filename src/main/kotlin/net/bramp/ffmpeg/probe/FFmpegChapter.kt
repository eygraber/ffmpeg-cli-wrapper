package net.bramp.ffmpeg.probe

import com.google.gson.annotations.SerializedName
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegChapter(
  val id: Long = 0,
  @SerializedName("time_base")
  val timeBase: String = "",
  val start: Long = 0,
  @SerializedName("start_time")
  val startTime: String = "",
  val end: Long = 0,
  @SerializedName("end_time")
  val endTime: String = "",
  val tags: FFmpegChapterTag? = null,
)
