package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.serde.CodecTypeSerializer
import net.bramp.ffmpeg.shared.CodecType

@Serializable
data class FFmpegPacket(
  var type: String? = null, // "packet" in packets_and_frames format
  @SerialName("codec_type") @Serializable(with = CodecTypeSerializer::class) var codecType: CodecType? = null,
  @SerialName("stream_index") var streamIndex: Int = 0,
  var pts: Long = 0,
  @SerialName("pts_time") var ptsTime: String? = null,
  var dts: Long = 0,
  @SerialName("dts_time") var dtsTime: String? = null,
  var duration: Long = 0,
  @SerialName("duration_time") var durationTime: String? = null,
  var size: String? = null,
  var pos: String? = null,
  @SerialName("flags") var flags: String? = null,
) : FFmpegFrameOrPacket {
  companion object {
    @JvmStatic
    fun fromJson(json: String): FFmpegPacket = FFmpegUtils.json.decodeFromString(serializer<FFmpegPacket>(), json)
  }
}
