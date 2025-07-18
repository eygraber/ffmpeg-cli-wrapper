package net.bramp.ffmpeg.serde

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.probe.FFmpegStream

object FFmpegStreamSideDataSerializer : KSerializer<FFmpegStream.SideData> {
  private val sideDataSerializer = serializer<FFmpegStream.SideData>()

  override val descriptor: SerialDescriptor = sideDataSerializer.descriptor

    override fun serialize(encoder: Encoder, value: FFmpegStream.SideData) {
      encoder.encodeSerializableValue(sideDataSerializer, value)
    }

    override fun deserialize(decoder: Decoder): FFmpegStream.SideData {
      return decoder.decodeSerializableValue(sideDataSerializer)
    }
}
