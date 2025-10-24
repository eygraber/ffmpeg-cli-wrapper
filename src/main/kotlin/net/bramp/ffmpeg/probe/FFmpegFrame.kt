package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.serialization.CodecTypeSerializer
import net.bramp.ffmpeg.shared.CodecType

@Serializable
data class FFmpegFrame(
  val type: String? = null, // "frame" in packets_and_frames format
  @SerialName("media_type") @Serializable(with = CodecTypeSerializer::class) val mediaType: CodecType? = null,
  @SerialName("stream_index") val streamIndex: Int = 0,
  @SerialName("key_frame") val keyFrame: Int = 0,
  @SerialName("pkt_pts") val pktPts: Long = 0,
  @SerialName("pkt_pts_time") val pktPtsTime: String? = null,
  @SerialName("pkt_dts") val pktDts: Long = 0,
  @SerialName("pkt_dts_time") val pktDtsTime: String? = null,
  @SerialName("best_effort_timestamp") val bestEffortTimestamp: Long = 0,
  @SerialName("best_effort_timestamp_time") val bestEffortTimestampTime: String? = null,
  @SerialName("pkt_duration") val pktDuration: Long = 0,
  @SerialName("pkt_duration_time") val pktDurationTime: String? = null,
  @SerialName("pkt_pos") val pktPos: Long = 0,
  @SerialName("pkt_size") val pktSize: Long = 0,
  val width: Int = 0,
  val height: Int = 0,
  @SerialName("pix_fmt") val pixFmt: String? = null,
  @SerialName("sample_aspect_ratio") val sampleAspectRatio: String? = null,
  @SerialName("pict_type") val pictType: String? = null,
  @SerialName("coded_picture_number") val codedPictureNumber: Long = 0,
  @SerialName("display_picture_number") val displayPictureNumber: Long = 0,
  @SerialName("interlaced_frame") val interlacedFrame: Int = 0,
  @SerialName("top_field_first") val topFieldFirst: Int = 0,
  @SerialName("repeat_pict") val repeatPict: Int = 0,
  @SerialName("color_range") val colorRange: String? = null,
  @SerialName("color_space") val colorSpace: String? = null,
  @SerialName("color_primaries") val colorPrimaries: String? = null,
  @SerialName("color_transfer") val colorTransfer: String? = null,
  @SerialName("chroma_location") val chromaLocation: String? = null,
  @SerialName("sample_fmt") val sampleFmt: String? = null,
  @SerialName("nb_samples") val nbSamples: Int = 0,
  val channels: Int = 0,
  @SerialName("channel_layout") val channelLayout: String? = null,
) : FFmpegFrameOrPacket {
  companion object {
    @JvmStatic
    fun fromJson(json: String): FFmpegFrame = FFmpegUtils.json.decodeFromString(serializer<FFmpegFrame>(), json)
  }
}
