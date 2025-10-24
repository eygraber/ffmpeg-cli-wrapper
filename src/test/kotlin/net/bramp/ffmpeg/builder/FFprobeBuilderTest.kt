package net.bramp.ffmpeg.builder

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test

class FFprobeBuilderTest {
  @Test
  fun testDefaultFFprobeConfiguration() {
    val args = FFprobeBuilder().setInput("input").build()

    args shouldBe listOf(
      "-v",
      "quiet",
      "-print_format",
      "json",
      "-show_error",
      "-show_format",
      "-show_streams",
      "-show_chapters",
      "input",
    )
  }

  @Test
  fun testPacketsAndFramesEnabled() {
    val args = FFprobeBuilder()
      .setInput("input")
      .setShowPackets(true)
      .setShowFrames(true)
      .build()

    args shouldBe listOf(
      "-v",
      "quiet",
      "-print_format",
      "json",
      "-show_error",
      "-show_format",
      "-show_streams",
      "-show_chapters",
      "-show_packets",
      "-show_frames",
      "input",
    )
  }

  @Test
  fun testDefaultOptionsDisabled() {
    val args = FFprobeBuilder()
      .setInput("input")
      .setShowChapters(false)
      .setShowStreams(false)
      .setShowFormat(false)
      .build()

    args shouldBe listOf("-v", "quiet", "-print_format", "json", "-show_error", "input")
  }

  @Test
  fun testSpecifyUserAgent() {
    val args = FFprobeBuilder()
      .setInput("input")
      .setShowChapters(false)
      .setShowStreams(false)
      .setShowFormat(false)
      .setUserAgent("user agent")
      .build()

    args shouldBe listOf(
      "-v",
      "quiet",
      "-print_format",
      "json",
      "-show_error",
      "-user_agent",
      "user agent",
      "input",
    )
  }

  // Note: throwsExceptionIfInputIsNull() from Java not needed in Kotlin
  // because the type system prevents null from being passed to setInput(filename: String)

  @Test
  fun throwsExceptionIfNoInputIsGiven() {
    val builder = FFprobeBuilder()
    shouldThrow<IllegalStateException> { builder.build() }
  }
}
