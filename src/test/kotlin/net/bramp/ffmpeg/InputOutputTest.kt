package net.bramp.ffmpeg

import io.mockk.every
import io.mockk.mockk
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.lang.MockProcess
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.TimeUnit

class InputOutputTest {
  private lateinit var runFunc: ProcessFunction
  private lateinit var ffmpeg: FFmpeg

  @Before
  @Throws(IOException::class)
  fun before() {
    runFunc = mockk()
    every { runFunc.run(match { it.contains("-version") }) } returns MockProcess(Helper.loadResource("avconv-version"))

    ffmpeg = FFmpeg(FFmpeg.DEFAULT_PATH, runFunc)
  }

  @Test
  fun setInputFormat() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .setFormat("mp4")
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(listOf("-y", "-v", "error", "-f", "mp4", "-i", "input.mp4", "output.mp4")),
    )
  }

  @Test
  fun setInputFormatMultiple() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .setFormat("mp4")
      .done()
      .addInput("input.mkv")
      .setFormat("matroschka")
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(
        listOf(
          "-y",
          "-v",
          "error",
          "-f",
          "mp4",
          "-i",
          "input.mp4",
          "-f",
          "matroschka",
          "-i",
          "input.mkv",
          "output.mp4",
        ),
      ),
    )
  }

  @Test
  fun setStartOffsetOnInput() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .setStartOffset(10, TimeUnit.SECONDS)
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(
        listOf(
          "-y",
          "-v",
          "error",
          "-ss",
          "00:00:10",
          "-i",
          "input.mp4",
          "output.mp4",
        ),
      ),
    )
  }

  @Test
  fun setStartOffsetOnOutput() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .setStartOffset(10, TimeUnit.SECONDS)
      .done()
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
          "-ss",
          "00:00:10",
          "output.mp4",
        ),
      ),
    )
  }

  @Test
  fun setStartOffsetOnInputAndOutput() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .setStartOffset(1, TimeUnit.SECONDS)
      .done()
      .addOutput("output.mp4")
      .setStartOffset(10, TimeUnit.SECONDS)
      .done()
      .build()

    assertThat(
      command,
      `is`(
        listOf(
          "-y",
          "-v",
          "error",
          "-ss",
          "00:00:01",
          "-i",
          "input.mp4",
          "-ss",
          "00:00:10",
          "output.mp4",
        ),
      ),
    )
  }

  @Test
  fun readAtNativeFrameRate() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .readAtNativeFrameRate()
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(listOf("-y", "-v", "error", "-re", "-i", "input.mp4", "output.mp4")),
    )
  }

  @Test
  fun readAtNativeFrameRateMultiple() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .readAtNativeFrameRate()
      .done()
      .addInput("input.mkv")
      .readAtNativeFrameRate()
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(
        listOf(
          "-y",
          "-v",
          "error",
          "-re",
          "-i",
          "input.mp4",
          "-re",
          "-i",
          "input.mkv",
          "output.mp4",
        ),
      ),
    )
  }

  @Test
  fun outputCodec() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .setVideoCodec("libx264")
      .setAudioCodec("aac")
      .setSubtitleCodec("vtt")
      .done()
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
          "-vcodec",
          "libx264",
          "-acodec",
          "aac",
          "-scodec",
          "vtt",
          "output.mp4",
        ),
      ),
    )
  }

  @Test
  fun inputCodec() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .setVideoCodec("libx264")
      .setAudioCodec("aac")
      .setSubtitleCodec("vtt")
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(
        listOf(
          "-y",
          "-v",
          "error",
          "-vcodec",
          "libx264",
          "-acodec",
          "aac",
          "-scodec",
          "vtt",
          "-i",
          "input.mp4",
          "output.mp4",
        ),
      ),
    )
  }

  @Test
  fun inputVideoDisabled() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .disableVideo()
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(listOf("-y", "-v", "error", "-vn", "-i", "input.mp4", "output.mp4")),
    )
  }

  @Test
  fun outputVideoDisabled() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .disableVideo()
      .done()
      .build()

    assertThat(
      command,
      `is`(listOf("-y", "-v", "error", "-i", "input.mp4", "-vn", "output.mp4")),
    )
  }

  @Test
  fun inputAudioDisabled() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .disableAudio()
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(listOf("-y", "-v", "error", "-an", "-i", "input.mp4", "output.mp4")),
    )
  }

  @Test
  fun outputAudioDisabled() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .disableAudio()
      .done()
      .build()

    assertThat(
      command,
      `is`(listOf("-y", "-v", "error", "-i", "input.mp4", "-an", "output.mp4")),
    )
  }

  @Test
  fun inputSubtitleDisabled() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .disableSubtitle()
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(listOf("-y", "-v", "error", "-sn", "-i", "input.mp4", "output.mp4")),
    )
  }

  @Test
  fun outputSubtitleDisabled() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .disableSubtitle()
      .done()
      .build()

    assertThat(
      command,
      `is`(listOf("-y", "-v", "error", "-i", "input.mp4", "-sn", "output.mp4")),
    )
  }

  @Test
  fun setExtraArgsToOnMultipleInputs() {
    val command = FFmpegBuilder()
      .addInput("input.mp4")
      .addExtraArgs("-t", "10")
      .done()
      .addInput("input.mkv")
      .addExtraArgs("-t", "20")
      .done()
      .addOutput("output.mp4")
      .done()
      .build()

    assertThat(
      command,
      `is`(
        listOf(
          "-y",
          "-v",
          "error",
          "-t",
          "10",
          "-i",
          "input.mp4",
          "-t",
          "20",
          "-i",
          "input.mkv",
          "output.mp4",
        ),
      ),
    )
  }

  @Test
  fun testAddExtraArgsOnInputsAndOutputs() {
    val command = FFmpegBuilder()
      .addExtraArgs("-global", "args")
      .setVerbosity(FFmpegBuilder.Verbosity.Info)
      .addInput("input.mp4")
      .addExtraArgs("-input_args", "1")
      .done()
      .addInput("input.mkv")
      .addExtraArgs("-input_args", "2")
      .done()
      .addOutput("output.mp4")
      .addExtraArgs("-output_args", "1")
      .done()
      .addOutput("output.mkv")
      .addExtraArgs("-output_args", "2")
      .done()
      .build()

    assertThat(
      command,
      `is`(
        listOf(
          "-y",
          "-v",
          "info",
          "-global",
          "args",
          "-input_args",
          "1",
          "-i",
          "input.mp4",
          "-input_args",
          "2",
          "-i",
          "input.mkv",
          "-output_args",
          "1",
          "output.mp4",
          "-output_args",
          "2",
          "output.mkv",
        ),
      ),
    )
  }
}
