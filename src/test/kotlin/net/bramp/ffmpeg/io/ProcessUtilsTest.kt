package net.bramp.ffmpeg.io

import net.bramp.ffmpeg.FFmpeg
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ProcessUtilsTest {
  @Test
  fun testProcessFinishesBeforeTimeout() {
    val ffmpeg = FFmpeg().path
    val processBuilder = ProcessBuilder(
      ffmpeg,
      "-y",
      "-v",
      "quiet",
      "-f",
      "lavfi",
      "-i",
      "testsrc=duration=1:size=1280x720:rate=10",
      "-c:v",
      "libx264",
      "-t",
      "1",
      "output.mp4",
    )
    val process = processBuilder.start()

    val exitValue = ProcessUtils.waitForWithTimeout(process, 5, TimeUnit.SECONDS)

    assertEquals(0, exitValue)
  }

  @Test
  fun testProcessDoesNotFinishBeforeTimeout() {
    val ffmpeg = FFmpeg().path
    val processBuilder = ProcessBuilder(
      ffmpeg,
      "-y",
      "-v",
      "quiet",
      "-f",
      "lavfi",
      "-i",
      "testsrc=duration=10:size=1280x720:rate=30",
      "-c:v",
      "libx264",
      "-t",
      "10",
      "output.mp4",
    )
    val process = processBuilder.start()

    assertThrows(TimeoutException::class.java) {
      ProcessUtils.waitForWithTimeout(process, 1, TimeUnit.MILLISECONDS)
    }

    process.destroy()
  }
}
