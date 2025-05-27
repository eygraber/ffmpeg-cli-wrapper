package net.bramp.commons.lang3.math.gson

import com.google.errorprone.annotations.Immutable
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
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
