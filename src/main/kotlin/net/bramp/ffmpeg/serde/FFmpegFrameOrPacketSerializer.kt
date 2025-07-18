package net.bramp.ffmpeg.serde

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.probe.FFmpegFrame
import net.bramp.ffmpeg.probe.FFmpegFrameOrPacket
import net.bramp.ffmpeg.probe.FFmpegPacket

object FFmpegFrameOrPacketSerializer : KSerializer<FFmpegFrameOrPacket> {
  private val frameSerializer = serializer<FFmpegFrame>()
  private val packetSerializer = serializer<FFmpegPacket>()

  override val descriptor: SerialDescriptor = frameSerializer.descriptor

  override fun serialize(encoder: Encoder, value: FFmpegFrameOrPacket) {
    if(value is FFmpegFrame) {
      encoder.encodeSerializableValue(frameSerializer, value)
    }
    else {
      encoder.encodeSerializableValue(packetSerializer, value as FFmpegPacket)
    }
  }

  override fun deserialize(decoder: Decoder): FFmpegFrameOrPacket {
    // Check if it's a frame or packet by looking at the "type" or "media_type" field
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    val jsonObject = element.jsonObject

    return if(jsonObject.containsKey("media_type")) {
      Json.decodeFromJsonElement(frameSerializer, element)
    }
    else {
      Json.decodeFromJsonElement(packetSerializer, element)
    }
  }
}

object FFmpegFrameOrPacketListSerializer : KSerializer<List<FFmpegFrameOrPacket>> {
  override val descriptor: SerialDescriptor = ListSerializer(FFmpegFrameOrPacketSerializer).descriptor

  override fun serialize(encoder: Encoder, value: List<FFmpegFrameOrPacket>) {
    encoder.encodeSerializableValue(ListSerializer(FFmpegFrameOrPacketSerializer), value)
  }

  override fun deserialize(decoder: Decoder): List<FFmpegFrameOrPacket> {
    return decoder.decodeSerializableValue(ListSerializer(FFmpegFrameOrPacketSerializer))
  }
}
