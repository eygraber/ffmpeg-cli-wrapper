package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.bramp.ffmpeg.serde.FFmpegFrameOrPacketListSerializer

@Serializable
data class FFmpegProbeResult @JvmOverloads constructor(
  var error: FFmpegError? = null,
  var format: FFmpegFormat? = null,
  var streams: List<FFmpegStream>? = emptyList(),
  var chapters: List<FFmpegChapter>? = emptyList(),
  var packets: List<FFmpegPacket>? = emptyList(),
  var frames: List<FFmpegFrame>? = emptyList(),
  @SerialName("packets_and_frames")
  @Serializable(with = FFmpegFrameOrPacketListSerializer::class)
  private var packetsAndFrames: List<FFmpegFrameOrPacket>? = null,
) {
  init {
    // If packets_and_frames is present, split it into packets and frames
    packetsAndFrames?.let { packetsAndFrames ->
      val splitPackets = mutableListOf<FFmpegPacket>()
      val splitFrames = mutableListOf<FFmpegFrame>()

      for(item in packetsAndFrames) {
        when(item) {
          is FFmpegPacket -> splitPackets.add(item)
          is FFmpegFrame -> splitFrames.add(item)
        }
      }

      if(packets.isNullOrEmpty()) {
        packets = splitPackets
      }
      if(frames.isNullOrEmpty()) {
        frames = splitFrames
      }
    }
  }

  fun hasError(): Boolean = error != null
}
