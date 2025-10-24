package net.bramp.ffmpeg.builder

import net.bramp.ffmpeg.builder.AbstractFFmpegStreamBuilder.DEVNULL
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
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

    assertThat(
      command,
      `is`(
        listOf(
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
          DEVNULL
        )
      )
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

    assertThat(
      command,
      `is`(
        listOf(
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
          "output.mp4"
        )
      )
    )
  }

  @Test(expected = IllegalArgumentException::class)
  fun firstPassNoBitrate() {
    FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .setFormat("mp4")
      .done()
      .setPass(1)
      .build()
  }

  @Test(expected = IllegalArgumentException::class)
  fun secondPassNoBitrate() {
    FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .setFormat("mp4")
      .done()
      .setPass(2)
      .build()
  }

  @Test(expected = IllegalArgumentException::class)
  fun firstPassNoFormat() {
    FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .setVideoBitRate(1_000_000)
      .done()
      .setPass(1)
      .build()
  }

  @Test(expected = IllegalArgumentException::class)
  fun secondPassNoFormat() {
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
