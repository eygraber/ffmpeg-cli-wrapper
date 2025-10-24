package net.bramp.ffmpeg.builder

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Test

class FFmpegFileInputBuilderTest : AbstractFFmpegInputBuilderTest() {
  override fun getBuilder(): AbstractFFmpegInputBuilder<*> = FFmpegBuilder().addInput("input.mp4")

  override fun removeCommon(command: List<String>): List<String> {
    assertEquals(command[command.size - 1], "input.mp4")
    assertEquals(command[command.size - 2], "-i")

    return command.subList(0, command.size - 2)
  }

  @Test
  fun testFileName() {
    val command = FFmpegBuilder().addInput("input.mp4").build(0)

    assertThat(command, `is`(listOf("-i", "input.mp4")))
  }
}
