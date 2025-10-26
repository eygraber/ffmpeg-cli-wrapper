package net.bramp.ffmpeg.kotlin.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FFmpegFormat(
  val filename: String? = null,
  @SerialName("nb_streams") val nbStreams: Int = 0,
  @SerialName("nb_programs") val nbPrograms: Int = 0,
  @SerialName("format_name") val formatName: String? = null,
  @SerialName("format_long_name") val formatLongName: String? = null,
  @SerialName("start_time") val startTime: Double? = null,
  @SerialName("duration") val duration: Double? = null,
  val size: Long = 0,
  @SerialName("bit_rate") val bitRate: Long = 0,
  @SerialName("probe_score") val probeScore: Int = 0,
  val tags: Map<String, String>? = null,
)
