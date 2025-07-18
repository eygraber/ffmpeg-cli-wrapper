package net.bramp.ffmpeg.serde

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Locale

class LowercaseEnumSerializer<T : Enum<T>>(
  private val enumValues: Array<T>,
) : KSerializer<T> {

  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("LowercaseEnumSerializer", PrimitiveKind.STRING)

  private val lowercaseToEnum = enumValues.associateBy { it.name.lowercase(Locale.ROOT) }

  override fun serialize(encoder: Encoder, value: T) {
    encoder.encodeString(value.name.lowercase(Locale.ROOT))
  }

  override fun deserialize(decoder: Decoder): T {
    val lowercaseString = decoder.decodeString().lowercase(Locale.ROOT)
    return lowercaseToEnum[lowercaseString]
      ?: throw IllegalArgumentException("Unknown enum value: $lowercaseString")
  }
}

inline fun <reified T : Enum<T>> LowercaseEnumSerializer(): LowercaseEnumSerializer<T> {
  return LowercaseEnumSerializer(enumValues<T>())
}

// Specific serializer for CodecType
object CodecTypeSerializer : KSerializer<net.bramp.ffmpeg.shared.CodecType> {
  private val delegate = LowercaseEnumSerializer(net.bramp.ffmpeg.shared.CodecType.values())

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: net.bramp.ffmpeg.shared.CodecType) {
    delegate.serialize(encoder, value)
  }

  override fun deserialize(decoder: Decoder): net.bramp.ffmpeg.shared.CodecType {
    return delegate.deserialize(decoder)
  }
}
