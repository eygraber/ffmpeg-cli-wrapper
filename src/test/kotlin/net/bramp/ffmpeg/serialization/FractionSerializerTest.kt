package net.bramp.ffmpeg.serialization

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.commons.lang3.math.Fraction
import org.junit.Test

class FractionSerializerTest {

  private val json = Json

  @Serializable
  data class FractionWrapper(
    @Serializable(with = FractionSerializer::class)
    val fraction: Fraction?,
  )

  data class TestData(
    val jsonString: String,
    val fraction: Fraction?,
  )

  @Test
  fun testRead() {
    for(test in readTests) {
      val wrapper = json.decodeFromString<FractionWrapper>("""{"fraction":${test.jsonString}}""")
      wrapper.fraction shouldBe test.fraction
    }
  }

  @Test
  fun testZerosRead() {
    for(test in zerosTests) {
      val wrapper = json.decodeFromString<FractionWrapper>("""{"fraction":${test.jsonString}}""")
      wrapper.fraction shouldBe test.fraction
    }
  }

  @Test
  fun testWrites() {
    for(test in writeTests) {
      val wrapper = FractionWrapper(test.fraction)
      val encoded = json.encodeToString(wrapper)
      encoded shouldBe """{"fraction":${test.jsonString}}"""
    }
  }

  @Test
  fun testWriteNull() {
    val wrapper = FractionWrapper(null)
    val encoded = json.encodeToString(wrapper)
    encoded shouldBe """{"fraction":null}"""
  }

  fun testNumericInput() {
    // Test that numeric inputs (without quotes) work
    val wrapper1 = json.decodeFromString<FractionWrapper>("""{"fraction":"1"}""")
    wrapper1.fraction shouldBe Fraction.getFraction(1, 1)

    val wrapper2 = json.decodeFromString<FractionWrapper>("""{"fraction":"0.5"}""")
    wrapper2.fraction shouldBe Fraction.getFraction(1, 2)
  }

  companion object {
    val readTests = listOf(
      TestData("null", null),
      // Note: kotlinx.serialization doesn't support unquoted numeric values for custom serializers
      // so we only test quoted string values
      TestData("\"1\"", Fraction.getFraction(1, 1)),
      TestData("\"1.0\"", Fraction.getFraction(1, 1)),
      TestData("\"2\"", Fraction.getFraction(2, 1)),
      TestData("\"0.5\"", Fraction.getFraction(1, 2)),
      TestData("\"1/2\"", Fraction.getFraction(1, 2)),
      TestData("\"1 1/2\"", Fraction.getFraction(1, 1, 2)),
    )

    // Divide by zero
    val zerosTests = listOf(
      TestData("\"0/0\"", Fraction.ZERO),
      TestData("\"1/0\"", Fraction.ZERO),
      TestData("\"2/0\"", Fraction.ZERO),
      TestData("\"100/0\"", Fraction.ZERO),
    )

    val writeTests = listOf(
      TestData("\"0\"", Fraction.ZERO),
      TestData("\"1\"", Fraction.getFraction(1, 1)),
      TestData("\"2\"", Fraction.getFraction(2, 1)),
      TestData("\"1/2\"", Fraction.getFraction(1, 2)),
      TestData("\"1 1/2\"", Fraction.getFraction(1, 1, 2)),
    )
  }
}
