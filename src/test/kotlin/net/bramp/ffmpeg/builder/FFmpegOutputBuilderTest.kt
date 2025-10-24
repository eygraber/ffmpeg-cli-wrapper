package net.bramp.ffmpeg.builder

import io.kotest.matchers.shouldBe

class FFmpegOutputBuilderTest : AbstractFFmpegOutputBuilderTest() {

  override fun getBuilder(): AbstractFFmpegOutputBuilder<*> = FFmpegBuilder().addInput(
    "input.mp4",
  ).done().addOutput("output.mp4")

  override fun removeCommon(command: List<String>): List<String> {
    command[command.size - 1] shouldBe "output.mp4"
    return command.subList(0, command.size - 1)
  }
}
