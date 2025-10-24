package net.bramp.ffmpeg.builder

import io.kotest.matchers.shouldBe
import org.junit.Test

abstract class AbstractFFmpegInputBuilderTest : AbstractFFmpegStreamBuilderTest() {
  protected abstract override fun getBuilder(): AbstractFFmpegInputBuilder<*>

  @Test
  fun testReadAtNativeFrameRate() {
    val command = getBuilder().readAtNativeFrameRate().build(0)

    removeCommon(command) shouldBe listOf("-re")
  }

  @Test
  fun testSetStreamLoopInfinit() {
    val command = getBuilder().setStreamLoop(-1).build(0)

    removeCommon(command) shouldBe listOf("-stream_loop", "-1")
  }

  @Test
  fun testSetStreamLoopCounter() {
    val command = getBuilder().setStreamLoop(2).build(0)

    removeCommon(command) shouldBe listOf("-stream_loop", "2")
  }
}
