package net.bramp.ffmpeg.serde

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.lang3.math.Fraction
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class FractionSerializerTest {

  private val json = Json

  @Serializable
  data class FractionWrapper(
    @Serializable(with = FractionSerializer::class)
    val fraction: Fraction,
  )

  data class TestData(
    val jsonString: String,
    val fraction: Fraction,
  )

  @Test
  fun testRead() {
    for(test in readTests) {
      val wrapper = json.decodeFromString<FractionWrapper>("""{"fraction":${test.jsonString}}""")
      assertThat(wrapper.fraction, equalTo(test.fraction))
    }
  }

  @Test
  fun testZerosRead() {
    for(test in zerosTests) {
      val wrapper = json.decodeFromString<FractionWrapper>("""{"fraction":${test.jsonString}}""")
      assertThat(wrapper.fraction, equalTo(test.fraction))
    }
  }

  @Test
  fun testWrites() {
    for(test in writeTests) {
      val wrapper = FractionWrapper(test.fraction)
      val encoded = json.encodeToString(wrapper)
      assertThat(encoded, equalTo("""{"fraction":${test.jsonString}}"""))
    }
  }

  @Test
  fun testNumericInput() {
    // Test that numeric inputs (without quotes) work
    val wrapper1 = json.decodeFromString<FractionWrapper>("""{"fraction":"1"}""")
    assertThat(wrapper1.fraction, equalTo(Fraction.getFraction(1, 1)))

    val wrapper2 = json.decodeFromString<FractionWrapper>("""{"fraction":"0.5"}""")
    assertThat(wrapper2.fraction, equalTo(Fraction.getFraction(1, 2)))
  }

  companion object {
    val readTests = listOf(
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
