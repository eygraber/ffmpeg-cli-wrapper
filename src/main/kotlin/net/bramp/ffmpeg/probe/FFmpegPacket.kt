package net.bramp.ffmpeg.probe

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import net.bramp.ffmpeg.shared.CodecType

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegPacket(
  val codec_type: CodecType? = null,
  val stream_index: Int = 0,
  val pts: Long = 0,
  val pts_time: Double = 0.0,
  val dts: Long = 0,
  val dts_time: Double = 0.0,
  val duration: Long = 0,
  val duration_time: Float = 0f,
  val size: String = "",
  val pos: String = "",
  val flags: String = "",
) : FFmpegFrameOrPacket
