package net.bramp.ffmpeg.kotlin.builder

import io.kotest.matchers.shouldBe
import org.junit.Test

class FFmpegFileInputBuilderTest : AbstractFFmpegInputBuilderTest() {
  override fun getBuilder(): AbstractFFmpegInputBuilder<*> = FFmpegBuilder().addInput("input.mp4")

  override fun removeCommon(command: List<String>): List<String> {
    command[command.size - 1] shouldBe "input.mp4"
    command[command.size - 2] shouldBe "-i"

    return command.subList(0, command.size - 2)
  }

  @Test
  fun testFileName() {
    val command = FFmpegBuilder().addInput("input.mp4").build(0)

    command shouldBe listOf("-i", "input.mp4")
  }
}
