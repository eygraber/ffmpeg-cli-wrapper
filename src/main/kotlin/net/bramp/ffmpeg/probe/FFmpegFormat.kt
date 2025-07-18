package net.bramp.ffmpeg.probe

import com.google.gson.annotations.SerializedName
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegFormat(
  val filename: String = "",
  @SerializedName("nb_streams")
  val nbStreams: Int = 0,
  @SerializedName("nb_programs")
  val nbPrograms: Int = 0,
  @SerializedName("format_name")
  val formatName: String = "",
  @SerializedName("format_long_name")
  val formatLongName: String = "",
  @SerializedName("start_time")
  val startTime: Double = 0.0,
  /** Duration in seconds  */
  val duration: Double = 0.0,
  /** File size in bytes  */
  val size: Long = 0,
  /** Bitrate  */
  @SerializedName("bit_rate")
  val bitRate: Long = 0,
  @SerializedName("probe_score")
  val probeScore: Int = 0,
  val tags: Map<String, String>? = null,
)
