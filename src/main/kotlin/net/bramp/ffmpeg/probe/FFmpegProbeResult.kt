package net.bramp.ffmpeg.probe

import com.google.common.collect.ImmutableList
import com.google.gson.annotations.SerializedName

data class FFmpegProbeResult @JvmOverloads constructor(
  val error: FFmpegError? = null,
  val format: FFmpegFormat? = null,
  val streams: List<FFmpegStream>? = null,
  val chapters: List<FFmpegChapter>? = emptyList(),
  @SerializedName("packets")
  val parsedPackets: List<FFmpegPacket>? = null,
  @SerializedName("frames")
  val parsedFrames: List<FFmpegFrame>? = null,
  @SerializedName("packets_and_frames")
  val packetsAndFrames: List<FFmpegFrameOrPacket>? = null,
) {
  private val actualPackets: List<FFmpegPacket> by lazy {
    if(parsedPackets == null) {
      packetsAndFrames?.filterIsInstance<FFmpegPacket>().orEmpty()
    }
    else {
      ImmutableList.copyOf(parsedPackets)
    }
  }

  private val actualFrames: List<FFmpegFrame> by lazy {
    if(parsedFrames == null) {
      packetsAndFrames?.filterIsInstance<FFmpegFrame>().orEmpty()
    }
    else {
      ImmutableList.copyOf(parsedFrames)
    }
  }

  fun hasError(): Boolean = error != null

  fun getPackets(): List<FFmpegPacket> = ImmutableList.copyOf(actualPackets)

  fun getFrames(): List<FFmpegFrame> = ImmutableList.copyOf(actualFrames)
}
