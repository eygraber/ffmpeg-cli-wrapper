package net.bramp.ffmpeg.builder

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

abstract class AbstractFFmpegOutputBuilderTest : AbstractFFmpegStreamBuilderTest() {

  protected abstract override fun getBuilder(): AbstractFFmpegOutputBuilder<*>

  @Test
  fun testConstantRateFactor() {
    val command = getBuilder().setConstantRateFactor(5.0).build(0)

    assertThat(removeCommon(command), `is`(listOf("-crf", "5")))
  }

  @Test
  fun testAudioSampleFormat() {
    val command = getBuilder().setAudioSampleFormat("asf").build(0)

    assertThat(removeCommon(command), `is`(listOf("-sample_fmt", "asf")))
  }

  @Test
  fun testAudioBitrate() {
    val command = getBuilder().setAudioBitRate(1_000).build(0)

    assertThat(removeCommon(command), `is`(listOf("-b:a", "1000")))
  }

  @Test
  fun testAudioQuality() {
    val command = getBuilder().setAudioQuality(5.0).build(0)

    assertThat(removeCommon(command), `is`(listOf("-qscale:a", "5")))
  }

  @Test
  fun testSetAudioBitStreamFilter() {
    val command = getBuilder().setAudioBitStreamFilter("filter").build(0)

    assertThat(removeCommon(command), `is`(listOf("-bsf:a", "filter")))
  }

  @Test
  fun testSetVideoBitRate() {
    val command = getBuilder().setVideoBitRate(1_000_000).build(0)

    assertThat(removeCommon(command), `is`(listOf("-b:v", "1000000")))
  }

  @Test
  fun testSetVideoQuality() {
    val command = getBuilder().setVideoQuality(20.0).build(0)

    assertThat(removeCommon(command), `is`(listOf("-qscale:v", "20")))
  }

  @Test
  fun testSetVideoPreset() {
    val command = getBuilder().setVideoPreset("main").build(0)

    assertThat(removeCommon(command), `is`(listOf("-vpre", "main")))
  }

  @Test
  fun testSetVideoFilter() {
    val command = getBuilder().setVideoFilter("filter").build(0)

    assertThat(removeCommon(command), `is`(listOf("-vf", "filter")))
  }

  @Test
  fun testSetVideoBitStreamFilter() {
    val command = getBuilder().setVideoBitStreamFilter("bit-stream-filter").build(0)

    assertThat(removeCommon(command), `is`(listOf("-bsf:v", "bit-stream-filter")))
  }

  @Test
  fun testSetComplexFilter() {
    val command = getBuilder().setComplexFilter("complex-filter").build(0)

    assertThat(removeCommon(command), `is`(listOf("-filter_complex", "complex-filter")))
  }

  @Test
  fun testSetBFrames() {
    val command = getBuilder().setBFrames(2).build(0)

    assertThat(removeCommon(command), `is`(listOf("-bf", "2")))
  }
}
