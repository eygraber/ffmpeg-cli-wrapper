package net.bramp.ffmpeg

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class FFmpegUtilsTest {

  @Test
  @Suppress("DEPRECATION")
  fun testMillisecondsToString() {
    assertEquals("00:01:03.123", FFmpegUtils.millisecondsToString(63_123))
    assertEquals("00:01:03", FFmpegUtils.millisecondsToString(63_000))
    assertEquals("01:23:45.678", FFmpegUtils.millisecondsToString(5_025_678))
    assertEquals("00:00:00", FFmpegUtils.millisecondsToString(0))
    assertEquals("00:00:00.001", FFmpegUtils.millisecondsToString(1))
  }

  @Test(expected = IllegalArgumentException::class)
  @Suppress("DEPRECATION")
  fun testMillisecondsToStringNegative() {
    FFmpegUtils.millisecondsToString(-1)
  }

  @Test(expected = IllegalArgumentException::class)
  @Suppress("DEPRECATION")
  fun testMillisecondsToStringNegativeMinValue() {
    FFmpegUtils.millisecondsToString(Long.MIN_VALUE)
  }

  @Test
  fun testToTimecode() {
    assertEquals("00:00:00", FFmpegUtils.toTimecode(0, TimeUnit.NANOSECONDS))
    assertEquals("00:00:00.000000001", FFmpegUtils.toTimecode(1, TimeUnit.NANOSECONDS))
    assertEquals("00:00:00.000001", FFmpegUtils.toTimecode(1, TimeUnit.MICROSECONDS))
    assertEquals("00:00:00.001", FFmpegUtils.toTimecode(1, TimeUnit.MILLISECONDS))
    assertEquals("00:00:01", FFmpegUtils.toTimecode(1, TimeUnit.SECONDS))
    assertEquals("00:01:00", FFmpegUtils.toTimecode(1, TimeUnit.MINUTES))
    assertEquals("01:00:00", FFmpegUtils.toTimecode(1, TimeUnit.HOURS))
  }

  @Test
  fun testFromTimecode() {
    assertEquals(63_123_000_000L, FFmpegUtils.fromTimecode("00:01:03.123"))
    assertEquals(63_000_000_000L, FFmpegUtils.fromTimecode("00:01:03"))
    assertEquals(5_025_678_000_000L, FFmpegUtils.fromTimecode("01:23:45.678"))
    assertEquals(0, FFmpegUtils.fromTimecode("00:00:00"))
  }

  @Test
  fun testParseBitrate() {
    assertEquals(12_300, FFmpegUtils.parseBitrate("12.3kbits/s"))
    assertEquals(1_000, FFmpegUtils.parseBitrate("1kbits/s"))
    assertEquals(123, FFmpegUtils.parseBitrate("0.123kbits/s"))
    assertEquals(-1, FFmpegUtils.parseBitrate("N/A"))
  }

  @Test(expected = IllegalArgumentException::class)
  fun testParseBitrateInvalidEmpty() {
    FFmpegUtils.parseBitrate("")
  }

  @Test(expected = IllegalArgumentException::class)
  fun testParseBitrateInvalidNumber() {
    FFmpegUtils.parseBitrate("12.3")
  }
}
