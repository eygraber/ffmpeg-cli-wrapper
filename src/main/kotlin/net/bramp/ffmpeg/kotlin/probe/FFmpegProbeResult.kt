package net.bramp.ffmpeg.kotlin.probe

import kotlinx.serialization.Serializable
import net.bramp.ffmpeg.kotlin.serialization.FFmpegProbeResultSerializer

@Serializable(with = FFmpegProbeResultSerializer::class)
data class FFmpegProbeResult @JvmOverloads constructor(
  val error: FFmpegError? = null,
  val format: FFmpegFormat? = null,
  val streams: List<FFmpegStream>? = emptyList(),
  val chapters: List<FFmpegChapter>? = emptyList(),
  val packets: List<FFmpegPacket>? = emptyList(),
  val frames: List<FFmpegFrame>? = emptyList(),
) {
  fun hasError(): Boolean = error != null
}
