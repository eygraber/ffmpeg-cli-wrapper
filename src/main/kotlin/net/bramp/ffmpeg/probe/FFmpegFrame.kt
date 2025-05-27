package net.bramp.ffmpeg.probe

import com.google.gson.annotations.SerializedName
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import net.bramp.ffmpeg.shared.CodecType

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegFrame(
  @SerializedName("media_type")
  val mediaType: CodecType? = null,
  @SerializedName("stream_index")
  val streamIndex: Int = 0,
  @SerializedName("key_frame")
  val keyFrame: Int = 0,
  @SerializedName("pkt_pts")
  val pktPts: Long = 0,
  @SerializedName("pkt_pts_time")
  val pktPtsTime: Double = 0.0,
  @SerializedName("pkt_dts")
  val pktDts: Long = 0,
  @SerializedName("pkt_dts_time")
  val pktDtsTime: Double = 0.0,
  @SerializedName("best_effort_timestamp")
  val bestEffortTimestamp: Long = 0,
  @SerializedName("best_effort_timestamp_time")
  val bestEffortTimestampTime: Float = 0f,
  @SerializedName("pkt_duration")
  val pktDuration: Long = 0,
  @SerializedName("pkt_duration_time")
  val pktDurationTime: Float = 0f,
  @SerializedName("pkt_pos")
  val pktPos: Long = 0,
  @SerializedName("pkt_size")
  val pktSize: Long = 0,
  @SerializedName("sample_fmt")
  val sampleFmt: String? = null,
  @SerializedName("nb_samples")
  val nbSamples: Int = 0,
  val channels: Int = 0,
  @SerializedName("channel_layout")
  val channelLayout: String? = null,
) : FFmpegFrameOrPacket
