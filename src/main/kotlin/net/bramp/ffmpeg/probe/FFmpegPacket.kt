package net.bramp.ffmpeg.probe

import com.google.gson.annotations.SerializedName
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import net.bramp.ffmpeg.shared.CodecType

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegPacket(
  @SerializedName("codec_type")
  val codecType: CodecType? = null,
  @SerializedName("stream_index")
  val streamIndex: Int = 0,
  val pts: Long = 0,
  @SerializedName("pts_time")
  val ptsTime: Double = 0.0,
  val dts: Long = 0,
  @SerializedName("dts_time")
  val dtsTime: Double = 0.0,
  val duration: Long = 0,
  @SerializedName("duration_time")
  val durationTime: Float = 0f,
  val size: String = "",
  val pos: String = "",
  val flags: String = "",
) : FFmpegFrameOrPacket
