package net.bramp.ffmpeg.serde

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.apache.commons.lang3.math.Fraction

object FractionSerializer : KSerializer<Fraction> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("Fraction", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Fraction) {
    encoder.encodeString(value.toProperString())
  }

  override fun deserialize(decoder: Decoder): Fraction {
    val fractionString = decoder.decodeString().trim()

    // Ambiguous as to what 0/0 is, but FFmpeg seems to think it's zero
    if(fractionString == "0/0") {
      return Fraction.ZERO
    }

    // Another edge case is invalid files sometimes output 1/0 or N/0
    if(fractionString.endsWith("/0")) {
      return Fraction.ZERO
    }

    // Try to parse as a double first (for numeric values)
    try {
      val doubleValue = fractionString.toDouble()
      return Fraction.getFraction(doubleValue)
    }
    catch(_: NumberFormatException) {
      // If it's not a double, parse as fraction string
    }

    return Fraction.getFraction(fractionString)
  }
}
