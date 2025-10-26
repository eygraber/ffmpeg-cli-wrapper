package net.bramp.ffmpeg.kotlin.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.kotlin.FFmpeg
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

    exitValue shouldBe 0
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

    shouldThrow<TimeoutException> {
      ProcessUtils.waitForWithTimeout(process, 1, TimeUnit.MILLISECONDS)
    }

    process.destroy()
  }
}
