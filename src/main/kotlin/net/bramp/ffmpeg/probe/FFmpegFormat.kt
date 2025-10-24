package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FFmpegFormat(
  var filename: String? = null,
  @SerialName("nb_streams") var nbStreams: Int = 0,
  @SerialName("nb_programs") var nbPrograms: Int = 0,
  @SerialName("format_name") var formatName: String? = null,
  @SerialName("format_long_name") var formatLongName: String? = null,
  @SerialName("start_time") var startTime: Double? = null,
  @SerialName("duration") var duration: Double? = null,
  var size: Long = 0,
  @SerialName("bit_rate") var bitRate: Long = 0,
  @SerialName("probe_score") var probeScore: Int = 0,
  var tags: Map<String, String>? = null,
)
