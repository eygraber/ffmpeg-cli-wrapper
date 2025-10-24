package net.bramp.ffmpeg.builder

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.builder.AbstractFFmpegStreamBuilder.Companion.DEVNULL
import org.junit.Test

class FFmpegBuilderTwoPassTest {

  @Test
  fun firstPass() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .setVideoBitRate(1_000_000)
      .setFormat("mp4")
      .done()
      .setPass(1)
      .build()

    command shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-an",
      "-i",
      "input.mp4",
      "-pass",
      "1",
      "-f",
      "mp4",
      "-b:v",
      "1000000",
      "-an",
      DEVNULL,
    )
  }

  @Test
  fun secondPass() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .setVideoBitRate(1_000_000)
      .setFormat("mp4")
      .done()
      .setPass(2)
      .build()

    command shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-i",
      "input.mp4",
      "-pass",
      "2",
      "-f",
      "mp4",
      "-b:v",
      "1000000",
      "output.mp4",
    )
  }

  @Test
  fun firstPassNoBitrate() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder()
        .addInput("input.mp4")
        .done()
        .addOutput("output.mp4")
        .setFormat("mp4")
        .done()
        .setPass(1)
        .build()
    }
  }

  @Test
  fun secondPassNoBitrate() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder()
        .addInput("input.mp4")
        .done()
        .addOutput("output.mp4")
        .setFormat("mp4")
        .done()
        .setPass(2)
        .build()
    }
  }

  @Test
  fun firstPassNoFormat() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder()
        .addInput("input.mp4")
        .done()
        .addOutput("output.mp4")
        .setVideoBitRate(1_000_000)
        .done()
        .setPass(1)
        .build()
    }
  }

  @Test
  fun secondPassNoFormat() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder()
        .addInput("input.mp4")
        .done()
        .addOutput("output.mp4")
        .setVideoBitRate(1_000_000)
        .done()
        .setPass(2)
        .build()
    }
  }
}
