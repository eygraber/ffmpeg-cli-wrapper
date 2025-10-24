package net.bramp.ffmpeg.builder

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

abstract class AbstractFFmpegInputBuilderTest : AbstractFFmpegStreamBuilderTest() {
  protected abstract override fun getBuilder(): AbstractFFmpegInputBuilder<*>

  @Test
  fun testReadAtNativeFrameRate() {
    val command = getBuilder().readAtNativeFrameRate().build(0)

    assertThat(removeCommon(command), `is`(listOf("-re")))
  }

  @Test
  fun testSetStreamLoopInfinit() {
    val command = getBuilder().setStreamLoop(-1).build(0)

    assertThat(removeCommon(command), `is`(listOf("-stream_loop", "-1")))
  }

  @Test
  fun testSetStreamLoopCounter() {
    val command = getBuilder().setStreamLoop(2).build(0)

    assertThat(removeCommon(command), `is`(listOf("-stream_loop", "2")))
  }
}
