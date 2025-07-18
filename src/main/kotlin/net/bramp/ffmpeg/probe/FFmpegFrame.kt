package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.shared.CodecType
import net.bramp.ffmpeg.serde.CodecTypeSerializer

@Serializable
data class FFmpegFrame(
  var type: String? = null, // "frame" in packets_and_frames format
  @SerialName("media_type") @Serializable(with = CodecTypeSerializer::class) var mediaType: CodecType? = null,
  @SerialName("stream_index") var streamIndex: Int = 0,
  @SerialName("key_frame") var keyFrame: Int = 0,
  @SerialName("pkt_pts") var pktPts: Long = 0,
  @SerialName("pkt_pts_time") var pktPtsTime: String? = null,
  @SerialName("pkt_dts") var pktDts: Long = 0,
  @SerialName("pkt_dts_time") var pktDtsTime: String? = null,
  @SerialName("best_effort_timestamp") var bestEffortTimestamp: Long = 0,
  @SerialName("best_effort_timestamp_time") var bestEffortTimestampTime: String? = null,
  @SerialName("pkt_duration") var pktDuration: Long = 0,
  @SerialName("pkt_duration_time") var pktDurationTime: String? = null,
  @SerialName("pkt_pos") var pktPos: Long = 0,
  @SerialName("pkt_size") var pktSize: Long = 0,
    var width: Int = 0,
    var height: Int = 0,
    @SerialName("pix_fmt") var pixFmt: String? = null,
    @SerialName("sample_aspect_ratio") var sampleAspectRatio: String? = null,
    @SerialName("pict_type") var pictType: String? = null,
    @SerialName("coded_picture_number") var codedPictureNumber: Long = 0,
    @SerialName("display_picture_number") var displayPictureNumber: Long = 0,
    @SerialName("interlaced_frame") var interlacedFrame: Int = 0,
    @SerialName("top_field_first") var topFieldFirst: Int = 0,
    @SerialName("repeat_pict") var repeatPict: Int = 0,
    @SerialName("color_range") var colorRange: String? = null,
    @SerialName("color_space") var colorSpace: String? = null,
    @SerialName("color_primaries") var colorPrimaries: String? = null,
    @SerialName("color_transfer") var colorTransfer: String? = null,
    @SerialName("chroma_location") var chromaLocation: String? = null,
  @SerialName("sample_fmt") var sampleFmt: String? = null,
  @SerialName("nb_samples") var nbSamples: Int = 0,
  var channels: Int = 0,
  @SerialName("channel_layout") var channelLayout: String? = null,
) : FFmpegFrameOrPacket {
    companion object {
        @JvmStatic
        fun fromJson(json: String): FFmpegFrame {
          return FFmpegUtils.json.decodeFromString(serializer<FFmpegFrame>(), json)
        }
    }
}
