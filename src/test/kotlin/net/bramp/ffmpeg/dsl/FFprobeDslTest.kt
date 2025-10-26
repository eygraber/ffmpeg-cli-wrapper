package net.bramp.ffmpeg.dsl

import io.kotest.matchers.shouldBe
import org.junit.Test

class FFprobeDslTest {
  @Test
  fun `test basic ffprobe DSL`() {
    val builder = ffprobe {
      input = "input.mp4"
    }

    val args = builder.build()
    args shouldBe listOf(
      "-v", "quiet", "-print_format", "json", "-show_error", "-show_format",
      "-show_streams", "-show_chapters", "input.mp4",
    )
  }

  @Test
  fun `test ffprobe DSL with custom flags`() {
    val builder = ffprobe {
      input = "input.mp4"
      showFormat = true
      showStreams = true
      showChapters = false
      showFrames = true
      showPackets = false
    }

    val args = builder.build()
    args shouldBe listOf(
      "-v", "quiet", "-print_format", "json", "-show_error", "-show_format",
      "-show_streams", "-show_frames", "input.mp4",
    )
  }

  @Test
  fun `test ffprobe DSL with user agent`() {
    val builder = ffprobe {
      input = "input.mp4"
      userAgent = "Mozilla/5.0"
    }

    val args = builder.build()
    args shouldBe listOf(
      "-v", "quiet", "-print_format", "json", "-show_error", "-user_agent",
      "Mozilla/5.0", "-show_format", "-show_streams", "-show_chapters", "input.mp4",
    )
  }

  @Test
  fun `test ffprobe DSL with extra args`() {
    val builder = ffprobe {
      input = "input.mp4"
      extraArgs("-select_streams", "v:0")
    }

    val args = builder.build()
    args shouldBe listOf(
      "-v", "quiet", "-print_format", "json", "-show_error", "-select_streams", "v:0",
      "-show_format", "-show_streams", "-show_chapters", "input.mp4",
    )
  }

  @Test
  fun `test ffprobe DSL with all disabled`() {
    val builder = ffprobe {
      input = "input.mp4"
      showFormat = false
      showStreams = false
      showChapters = false
    }

    val args = builder.build()
    args shouldBe listOf(
      "-v", "quiet", "-print_format", "json", "-show_error", "input.mp4",
    )
  }

  @Test
  fun `test ffprobe DSL with packets and frames`() {
    val builder = ffprobe {
      input = "input.mp4"
      showPackets = true
      showFrames = true
    }

    val args = builder.build()
    args shouldBe listOf(
      "-v", "quiet", "-print_format", "json", "-show_error", "-show_format",
      "-show_streams", "-show_chapters", "-show_packets", "-show_frames", "input.mp4",
    )
  }

  @Test
  fun `test buildFFprobeCommand helper`() {
    val args = buildFFprobeCommand {
      input = "input.mp4"
      showFormat = true
    }

    args shouldBe listOf(
      "-v", "quiet", "-print_format", "json", "-show_error", "-show_format",
      "-show_streams", "-show_chapters", "input.mp4",
    )
  }
}
