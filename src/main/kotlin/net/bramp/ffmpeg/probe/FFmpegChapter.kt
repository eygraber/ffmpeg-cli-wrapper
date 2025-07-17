package net.bramp.ffmpeg.probe

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegChapter(
  val id: Long = 0,
  val time_base: String = "",
  val start: Long = 0,
  val start_time: String = "",
  val end: Long = 0,
  val end_time: String = "",
  val tags: FFmpegChapterTag? = null,
)
