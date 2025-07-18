package net.bramp.commons.lang3.math.gson

import com.google.errorprone.annotations.Immutable
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.apache.commons.lang3.math.Fraction
import java.io.IOException

/**
 * GSON TypeAdapter for Apache Commons Math Fraction Object
 *
 * @author bramp
 */
@Immutable
class FractionAdapter(
  /** If set, 0/0 returns this value, instead of throwing a ArithmeticException */
  private val zeroByZero: Fraction = Fraction.ZERO,
  /** If set, N/0 returns this value, instead of throwing a ArithmeticException */
  private val divideByZero: Fraction = Fraction.ZERO,
) : TypeAdapter<Fraction>() {

  @Suppress("ReturnCount")
  @Throws(IOException::class)
  override fun read(reader: JsonReader): Fraction? {
    val next = reader.peek()

    if(next == JsonToken.NULL) {
      reader.nextNull()
      return null
    }

    if(next == JsonToken.NUMBER) {
      return Fraction.getFraction(reader.nextDouble())
    }

    val fractionString = reader.nextString().trim()

    // Ambiguous as to what 0/0 is, but FFmpeg seems to think it's zero
    if("0/0" == fractionString) {
      return zeroByZero
    }

    // Another edge cases is invalid files sometimes output 1/0.
    if(fractionString.endsWith("/0")) {
      return divideByZero
    }

    return Fraction.getFraction(fractionString)
  }

  @Throws(IOException::class)
  override fun write(writer: JsonWriter, value: Fraction?) {
    if(value == null) {
      writer.nullValue()
      return
    }
    writer.value(value.toProperString())
  }
}

/**
 * Kotlinx Serialization KSerializer for Apache Commons Math Fraction Object
 *
 */
@OptIn(ExperimentalSerializationApi::class)
class FractionSerializer(
  /** If set, 0/0 returns this value, instead of throwing a ArithmeticException */
  private val zeroByZero: Fraction = Fraction.ZERO,
  /** If set, N/0 returns this value, instead of throwing a ArithmeticException */
  private val divideByZero: Fraction = Fraction.ZERO,
) : KSerializer<Fraction?> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("net.bramp.commons.lang3.math.gson.Fraction", PrimitiveKind.STRING)

  @Suppress("ReturnCount")
  override fun deserialize(decoder: Decoder): Fraction? {
    if(!decoder.decodeNotNullMark()) return null

    val value = decoder.decodeString()

    // Try to parse as a double first
    try {
      return Fraction.getFraction(value.toDouble())
    }
    catch(_: NumberFormatException) {
      // If it's not a double, proceed to string parsing
    }

    val fractionString = value.trim()

    return when {
      // Ambiguous as to what 0/0 is, but FFmpeg seems to think it's zero
      fractionString == "0/0" -> zeroByZero

      // Another edge cases is invalid files sometimes output 1/0.
      fractionString.endsWith("/0") -> divideByZero

      else -> Fraction.getFraction(fractionString)
    }
  }

  override fun serialize(encoder: Encoder, value: Fraction?) {
    if(value == null) {
      encoder.encodeNull()
    }
    else {
      encoder.encodeNotNullMark()
      encoder.encodeString(value.toProperString())
    }
  }
}
