package net.bramp.ffmpeg.probe

import com.google.common.collect.ImmutableList
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

/** TODO Make this immutable  */
@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegProbeResult(
  var error: FFmpegError? = null,
  val format: FFmpegFormat? = null,
  val streams: List<FFmpegStream>? = null,
  val chapters: List<FFmpegChapter>? = emptyList(),
  val packets_and_frames: List<FFmpegFrameOrPacket>? = null,
) {
  private var packets: List<FFmpegPacket>? = null
  private var frames: List<FFmpegFrame>? = null

  fun hasError(): Boolean = error != null

  fun getPackets(): List<FFmpegPacket> {
    if(packets == null) {
      packets = packets_and_frames?.filterIsInstance<FFmpegPacket>() ?: emptyList()
    }
    return ImmutableList.copyOf(packets)
  }

  fun getFrames(): List<FFmpegFrame> {
    if(frames == null) {
      frames = packets_and_frames?.filterIsInstance<FFmpegFrame>() ?: emptyList()
    }
    return ImmutableList.copyOf(frames)
  }
}
