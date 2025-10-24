package net.bramp.ffmpeg

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.util.concurrent.TimeUnit

class FFmpegUtilsTest {

  @Test
  @Suppress("DEPRECATION")
  fun testMillisecondsToString() {
    FFmpegUtils.millisecondsToString(63_123) shouldBe "00:01:03.123"
    FFmpegUtils.millisecondsToString(63_000) shouldBe "00:01:03"
    FFmpegUtils.millisecondsToString(5_025_678) shouldBe "01:23:45.678"
    FFmpegUtils.millisecondsToString(0) shouldBe "00:00:00"
    FFmpegUtils.millisecondsToString(1) shouldBe "00:00:00.001"
  }

  @Test
  @Suppress("DEPRECATION")
  fun testMillisecondsToStringNegative() {
    shouldThrow<IllegalArgumentException> {
      FFmpegUtils.millisecondsToString(-1)
    }
  }

  @Test
  @Suppress("DEPRECATION")
  fun testMillisecondsToStringNegativeMinValue() {
    shouldThrow<IllegalArgumentException> {
      FFmpegUtils.millisecondsToString(Long.MIN_VALUE)
    }
  }

  @Test
  fun testToTimecode() {
    FFmpegUtils.toTimecode(0, TimeUnit.NANOSECONDS) shouldBe "00:00:00"
    FFmpegUtils.toTimecode(1, TimeUnit.NANOSECONDS) shouldBe "00:00:00.000000001"
    FFmpegUtils.toTimecode(1, TimeUnit.MICROSECONDS) shouldBe "00:00:00.000001"
    FFmpegUtils.toTimecode(1, TimeUnit.MILLISECONDS) shouldBe "00:00:00.001"
    FFmpegUtils.toTimecode(1, TimeUnit.SECONDS) shouldBe "00:00:01"
    FFmpegUtils.toTimecode(1, TimeUnit.MINUTES) shouldBe "00:01:00"
    FFmpegUtils.toTimecode(1, TimeUnit.HOURS) shouldBe "01:00:00"
  }

  @Test
  fun testFromTimecode() {
    FFmpegUtils.fromTimecode("00:01:03.123") shouldBe 63_123_000_000L
    FFmpegUtils.fromTimecode("00:01:03") shouldBe 63_000_000_000L
    FFmpegUtils.fromTimecode("01:23:45.678") shouldBe 5_025_678_000_000L
    FFmpegUtils.fromTimecode("00:00:00") shouldBe 0
  }

  @Test
  fun testParseBitrate() {
    FFmpegUtils.parseBitrate("12.3kbits/s") shouldBe 12_300
    FFmpegUtils.parseBitrate("1kbits/s") shouldBe 1_000
    FFmpegUtils.parseBitrate("0.123kbits/s") shouldBe 123
    FFmpegUtils.parseBitrate("N/A") shouldBe -1
  }

  @Test
  fun testParseBitrateInvalidEmpty() {
    shouldThrow<IllegalArgumentException> {
      FFmpegUtils.parseBitrate("")
    }
  }

  @Test
  fun testParseBitrateInvalidNumber() {
    shouldThrow<IllegalArgumentException> {
      FFmpegUtils.parseBitrate("12.3")
    }
  }
}
