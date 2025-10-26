package net.bramp.ffmpeg.kotlin.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.kotlin.probe.FFmpegChapter
import net.bramp.ffmpeg.kotlin.probe.FFmpegError
import net.bramp.ffmpeg.kotlin.probe.FFmpegFormat
import net.bramp.ffmpeg.kotlin.probe.FFmpegFrame
import net.bramp.ffmpeg.kotlin.probe.FFmpegFrameOrPacket
import net.bramp.ffmpeg.kotlin.probe.FFmpegPacket
import net.bramp.ffmpeg.kotlin.probe.FFmpegProbeResult
import net.bramp.ffmpeg.kotlin.probe.FFmpegStream

@OptIn(ExperimentalSerializationApi::class)
object FFmpegProbeResultSerializer : KSerializer<FFmpegProbeResult> {
  private val errorSerializer = serializer<FFmpegError>().nullable
  private val formatSerializer = serializer<FFmpegFormat>().nullable
  private val streamsSerializer = ListSerializer(serializer<FFmpegStream>()).nullable
  private val chaptersSerializer = ListSerializer(serializer<FFmpegChapter>()).nullable
  private val packetsSerializer = ListSerializer(serializer<FFmpegPacket>()).nullable
  private val framesSerializer = ListSerializer(serializer<FFmpegFrame>()).nullable
  private val packetsAndFramesSerializer = ListSerializer(FFmpegFrameOrPacketSerializer).nullable

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FFmpegProbeResult") {
    element<FFmpegError?>("error")
    element<FFmpegFormat?>("format")
    element<List<FFmpegStream>?>("streams")
    element<List<FFmpegChapter>?>("chapters")
    element<List<FFmpegPacket>?>("packets")
    element<List<FFmpegFrame>?>("frames")
    element<List<FFmpegFrameOrPacket>?>("packets_and_frames")
  }

  override fun serialize(encoder: Encoder, value: FFmpegProbeResult) {
    encoder.encodeStructure(descriptor) {
      encodeNullableSerializableElement(descriptor, 0, errorSerializer, value.error)
      encodeNullableSerializableElement(descriptor, 1, formatSerializer, value.format)
      encodeNullableSerializableElement(descriptor, 2, streamsSerializer, value.streams)
      encodeNullableSerializableElement(descriptor, 3, chaptersSerializer, value.chapters)
      encodeNullableSerializableElement(descriptor, 4, packetsSerializer, value.packets)
      encodeNullableSerializableElement(descriptor, 5, framesSerializer, value.frames)
    }
  }

  override fun deserialize(decoder: Decoder): FFmpegProbeResult =
    decoder.decodeStructure(descriptor) {
      var error: FFmpegError? = null
      var format: FFmpegFormat? = null
      var streams: List<FFmpegStream>? = emptyList()
      var chapters: List<FFmpegChapter>? = emptyList()
      var packets: List<FFmpegPacket>? = emptyList()
      var frames: List<FFmpegFrame>? = emptyList()
      var packetsAndFrames: List<FFmpegFrameOrPacket>? = null

      while(true) {
        when(val index = decodeElementIndex(descriptor)) {
          0 -> error = decodeNullableSerializableElement(descriptor, 0, errorSerializer)
          1 -> format = decodeNullableSerializableElement(descriptor, 1, formatSerializer)
          2 -> streams = decodeNullableSerializableElement(descriptor, 2, streamsSerializer)
          3 -> chapters = decodeNullableSerializableElement(descriptor, 3, chaptersSerializer)
          4 -> packets = decodeNullableSerializableElement(descriptor, 4, packetsSerializer)
          5 -> frames = decodeNullableSerializableElement(descriptor, 5, framesSerializer)
          6 -> packetsAndFrames = decodeNullableSerializableElement(descriptor, 6, packetsAndFramesSerializer)
          -1 -> break
          else -> error("Unexpected index: $index")
        }
      }

      // If packets_and_frames is present, split it
      val finalPackets = if(packets.isNullOrEmpty() && packetsAndFrames != null) {
        packetsAndFrames.filterIsInstance<FFmpegPacket>()
      }
      else {
        packets
      }

      val finalFrames = if(frames.isNullOrEmpty() && packetsAndFrames != null) {
        packetsAndFrames.filterIsInstance<FFmpegFrame>()
      }
      else {
        frames
      }

      FFmpegProbeResult(
        error = error,
        format = format,
        streams = streams,
        chapters = chapters,
        packets = finalPackets,
        frames = finalFrames,
      )
    }
}
