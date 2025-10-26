package net.bramp.ffmpeg.dsl

import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.builder.Strict
import org.junit.Test
import java.net.URI
import java.util.concurrent.TimeUnit

class FFmpegDslTest {
  @Test
  fun `test basic ffmpeg DSL`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        videoCodec = "libx264"
        audioCodec = "aac"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-vcodec", "libx264", "-acodec", "aac",
      "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with video settings`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        format = "mp4"
        videoCodec = "libx264"
        videoResolution(640, 480)
        videoFrameRate(30, 1)
        videoBitRate = 1_000_000
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-f", "mp4", "-vcodec", "libx264",
      "-s", "640x480", "-r", "30/1", "-b:v", "1000000", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with audio settings`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        audioCodec = "aac"
        audioChannels = 2
        audioSampleRate = 48_000
        audioBitRate = 128_000
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-acodec", "aac", "-ac", "2",
      "-ar", "48000", "-b:a", "128000", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with start offset and duration`() {
    val builder = ffmpeg {
      input("input.mp4") {
        startOffset(1500, TimeUnit.MILLISECONDS)
      }
      output("output.mp4") {
        duration(30, TimeUnit.SECONDS)
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-ss", "00:00:01.5", "-i", "input.mp4",
      "-t", "00:00:30", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with multiple inputs and outputs`() {
    val builder = ffmpeg {
      input("input1.mp4")
      input("input2.mp4")
      output("output1.mp4") {
        videoCodec = "libx264"
      }
      output("output2.mp4") {
        videoCodec = "libx265"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input1.mp4", "-i", "input2.mp4",
      "-vcodec", "libx264", "output1.mp4", "-vcodec", "libx265", "output2.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with filters`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        videoFilter = "scale=640:480"
        audioFilter = "volume=0.5"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-vf", "scale=640:480",
      "-af", "volume=0.5", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with disabled streams`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        disableAudio()
        disableSubtitle()
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-an", "-sn", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with metadata`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        metadata("title", "My Video")
        metadata("author", "John Doe")
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-metadata", "title=My Video",
      "-metadata", "author=John Doe", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with extra args`() {
    val builder = ffmpeg {
      extraArgs("-threads", "4")
      input("input.mp4")
      output("output.mp4") {
        extraArgs("-map", "0:0")
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-threads", "4", "-i", "input.mp4",
      "-map", "0:0", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with URI output`() {
    val builder = ffmpeg {
      input("input.mp4")
      output(URI.create("udp://10.1.0.102:1234")) {
        videoCodec = "libx264"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-vcodec", "libx264",
      "udp://10.1.0.102:1234",
    )
  }

  @Test
  fun `test ffmpeg DSL with HLS output`() {
    val builder = ffmpeg {
      input("input.mp4")
      hlsOutput("output.m3u8") {
        hlsTime(10, TimeUnit.SECONDS)
        hlsSegmentFilename = "segment%03d.ts"
        hlsListSize = 5
        videoCodec = "libx264"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-f", "hls", "-vcodec", "libx264",
      "-hls_time", "00:00:10", "-hls_segment_filename", "segment%03d.ts",
      "-hls_list_size", "5", "output.m3u8",
    )
  }

  @Test
  fun `test ffmpeg DSL with verbosity`() {
    val builder = ffmpeg {
      verbosity = FFmpegBuilder.Verbosity.Debug
      input("input.mp4")
      output("output.mp4")
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "debug", "-i", "input.mp4", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with strict mode`() {
    val builder = ffmpeg {
      strict = Strict.Experimental
      input("input.mp4")
      output("output.mp4")
    }

    val args = builder.build()
    args shouldBe listOf(
      "-strict", "experimental", "-y", "-v", "error", "-i", "input.mp4", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with user agent`() {
    val builder = ffmpeg {
      userAgent = "Mozilla/5.0"
      input("input.mp4")
      output("output.mp4")
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-user_agent", "Mozilla/5.0", "-i", "input.mp4", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with threads`() {
    val builder = ffmpeg {
      threads = 4
      input("input.mp4")
      output("output.mp4")
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-threads", "4", "-i", "input.mp4", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with input options`() {
    val builder = ffmpeg {
      input("input.mp4") {
        format = "mp4"
        readAtNativeFrameRate()
        streamLoop = 2
      }
      output("output.mp4")
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-re", "-stream_loop", "2", "-f", "mp4",
      "-i", "input.mp4", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with video quality`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        videoCodec = "libx264"
        videoQuality = 23.0
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-vcodec", "libx264",
      "-qscale:v", "23", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with CRF`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        videoCodec = "libx264"
        constantRateFactor = 23.0
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-crf", "23", "-vcodec", "libx264",
      "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with preset`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        videoCodec = "libx264"
        preset = "fast"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-preset", "fast",
      "-vcodec", "libx264", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with pixel format`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        videoCodec = "libx264"
        videoPixelFormat = "yuv420p"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-vcodec", "libx264",
      "-pix_fmt", "yuv420p", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with complex filter`() {
    val builder = ffmpeg {
      input("input.mp4")
      output("output.mp4") {
        complexFilter = "[0:v]scale=640:480[out]"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-filter_complex",
      "[0:v]scale=640:480[out]", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL stdout output`() {
    val builder = ffmpeg {
      input("input.mp4")
      stdoutOutput {
        format = "mp4"
      }
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-f", "mp4", "-",
    )
  }

  @Test
  fun `test buildFFmpegCommand helper`() {
    val args = buildFFmpegCommand {
      input("input.mp4")
      output("output.mp4") {
        videoCodec = "libx264"
      }
    }

    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp4", "-vcodec", "libx264", "output.mp4",
    )
  }

  @Test
  fun `test ffmpeg DSL with VBR`() {
    val builder = ffmpeg {
      input("input.mp3")
      vbr(2)
      output("output.mp3")
    }

    val args = builder.build()
    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input.mp3", "-qscale:a", "2", "output.mp3",
    )
  }

  @Test
  fun `test ffmpeg DSL with override output files false`() {
    val builder = ffmpeg {
      overrideOutputFiles = false
      input("input.mp4")
      output("output.mp4")
    }

    val args = builder.build()
    args shouldBe listOf(
      "-n", "-v", "error", "-i", "input.mp4", "output.mp4",
    )
  }
}
