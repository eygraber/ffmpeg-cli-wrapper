package net.bramp.ffmpeg.probe

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import net.bramp.ffmpeg.shared.CodecType

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegFrame(
  val media_type: CodecType? = null,
  val stream_index: Int = 0,
  val key_frame: Int = 0,
  val pkt_pts: Long = 0,
  val pkt_pts_time: Double = 0.0,
  val pkt_dts: Long = 0,
  val pkt_dts_time: Double = 0.0,
  val best_effort_timestamp: Long = 0,
  val best_effort_timestamp_time: Float = 0f,
  val pkt_duration: Long = 0,
  val pkt_duration_time: Float = 0f,
  val pkt_pos: Long = 0,
  val pkt_size: Long = 0,
  val sample_fmt: String? = null,
  val nb_samples: Int = 0,
  val channels: Int = 0,
  val channel_layout: String? = null,
) : FFmpegFrameOrPacket
