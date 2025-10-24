package net.bramp.ffmpeg.builder

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class FormatDecimalIntegerTest(
  private val input: Double,
  private val expected: String,
) {

  @Test
  fun formatDecimalInteger() {
    val got = AbstractFFmpegOutputBuilder.formatDecimalInteger(input)
    assertThat(got, equalTo(expected))
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data(): List<Array<Any>> = listOf(
      arrayOf(0.0, "0"),
      arrayOf(1.0, "1"),
      arrayOf(-1.0, "-1"),
      arrayOf(0.1, "0.1"),
      arrayOf(1.1, "1.1"),
      arrayOf(1.10, "1.1"),
      arrayOf(1.001, "1.001"),
      arrayOf(100.0, "100"),
      arrayOf(100.01, "100.01")
    )
  }
}
