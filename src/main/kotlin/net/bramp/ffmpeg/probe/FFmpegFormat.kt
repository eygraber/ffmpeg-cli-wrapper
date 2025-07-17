package net.bramp.ffmpeg.probe

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegFormat(
  val filename: String = "",
  val nb_streams: Int = 0,
  val nb_programs: Int = 0,
  val format_name: String = "",
  val format_long_name: String = "",
  val start_time: Double = 0.0,
  /** Duration in seconds  */
  val duration: Double = 0.0,
  /** File size in bytes  */
  val size: Long = 0,
  /** Bitrate  */
  val bit_rate: Long = 0,
  val probe_score: Int = 0,
  val tags: Map<String, String>? = null,
)
