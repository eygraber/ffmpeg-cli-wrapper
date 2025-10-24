package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.serde.CodecTypeSerializer
import net.bramp.ffmpeg.shared.CodecType

@Serializable
data class FFmpegPacket(
  val type: String? = null, // "packet" in packets_and_frames format
  @SerialName("codec_type") @Serializable(with = CodecTypeSerializer::class) val codecType: CodecType? = null,
  @SerialName("stream_index") val streamIndex: Int = 0,
  val pts: Long = 0,
  @SerialName("pts_time") val ptsTime: String? = null,
  val dts: Long = 0,
  @SerialName("dts_time") val dtsTime: String? = null,
  val duration: Long = 0,
  @SerialName("duration_time") val durationTime: String? = null,
  val size: String? = null,
  val pos: String? = null,
  @SerialName("flags") val flags: String? = null,
) : FFmpegFrameOrPacket {
  companion object {
    @JvmStatic
    fun fromJson(json: String): FFmpegPacket = FFmpegUtils.json.decodeFromString(serializer<FFmpegPacket>(), json)
  }
}
