package net.bramp.ffmpeg.builder

import org.junit.Assert.assertEquals

class FFmpegOutputBuilderTest : AbstractFFmpegOutputBuilderTest() {

  override fun getBuilder(): AbstractFFmpegOutputBuilder<*> {
    return FFmpegBuilder().addInput("input.mp4").done().addOutput("output.mp4")
  }

  override fun removeCommon(command: List<String>): List<String> {
    assertEquals("output.mp4", command[command.size - 1])
    return command.subList(0, command.size - 1)
  }
}
