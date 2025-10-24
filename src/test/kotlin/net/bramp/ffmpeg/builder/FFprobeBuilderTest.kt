package net.bramp.ffmpeg.builder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FFprobeBuilderTest {
  @Test
  fun testDefaultFFprobeConfiguration() {
    val args = FFprobeBuilder().setInput("input").build()

    assertEquals(
      args,
      listOf(
        "-v",
        "quiet",
        "-print_format",
        "json",
        "-show_error",
        "-show_format",
        "-show_streams",
        "-show_chapters",
        "input"
      )
    )
  }

  @Test
  fun testPacketsAndFramesEnabled() {
    val args = FFprobeBuilder()
      .setInput("input")
      .setShowPackets(true)
      .setShowFrames(true)
      .build()

    assertEquals(
      args,
      listOf(
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
        "input"
      )
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

    assertEquals(
      args,
      listOf("-v", "quiet", "-print_format", "json", "-show_error", "input")
    )
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

    assertEquals(
      args,
      listOf(
        "-v",
        "quiet",
        "-print_format",
        "json",
        "-show_error",
        "-user_agent",
        "user agent",
        "input"
      )
    )
  }

  @Test
  fun throwsExceptionIfNoInputIsGiven() {
    val builder = FFprobeBuilder()
    assertThrows(IllegalStateException::class.java) { builder.build() }
  }
}
