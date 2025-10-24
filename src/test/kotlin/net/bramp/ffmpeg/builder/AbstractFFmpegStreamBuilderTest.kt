package net.bramp.ffmpeg.builder

import org.apache.commons.lang3.math.Fraction
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

abstract class AbstractFFmpegStreamBuilderTest {
  protected abstract fun getBuilder(): AbstractFFmpegStreamBuilder<*>

  protected abstract fun removeCommon(command: List<String>): List<String>

  @Test
  fun testSetFormat() {
    val builder = getBuilder()
    builder.setFormat("mp4")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-f", "mp4")))
  }

  @Test
  fun testSetStartOffset() {
    val builder = getBuilder()
    builder.setStartOffset(10, TimeUnit.SECONDS)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-ss", "00:00:10")))
  }

  @Test
  fun testSetDuration() {
    val builder = getBuilder()
    builder.setDuration(5, TimeUnit.SECONDS)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-t", "00:00:05")))
  }

  @Test
  fun testAddMetaTagKeyValue() {
    val builder = getBuilder()
    builder.addMetaTag("key", "value")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-metadata", "key=value")))
  }

  @Test
  fun testAddMetaTagSpecKeyValue() {
    val builder = getBuilder()
    builder.addMetaTag(MetadataSpecifier.stream(1), "key", "value")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-metadata:s:1", "key=value")))
  }

  @Test
  fun testDisableAudio() {
    val builder = getBuilder()
    builder.disableAudio()
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-an")))
  }

  @Test
  fun testSetAudioCodec() {
    val builder = getBuilder()
    builder.setAudioCodec("acc")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-acodec", "acc")))
  }

  @Test
  fun testSetAudioChannels() {
    val builder = getBuilder()
    builder.setAudioChannels(7)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-ac", "7")))
  }

  @Test
  fun testSetAudioSampleRate() {
    val builder = getBuilder()
    builder.setAudioSampleRate(44100)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-ar", "44100")))
  }

  @Test
  fun testSetAudioPreset() {
    val builder = getBuilder()
    builder.setAudioPreset("ac")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-apre", "ac")))
  }

  @Test
  fun testDisableVideo() {
    val builder = getBuilder()
    builder.disableVideo()
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-vn")))
  }

  @Test
  fun testSetVideoCodec() {
    val builder = getBuilder()
    builder.setVideoCodec("libx264")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-vcodec", "libx264")))
  }

  @Test
  fun testSetVideoCopyInkf() {
    val builder = getBuilder()
    builder.setVideoCopyInkf(true)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-copyinkf")))
  }

  @Test
  fun testSetVideoFrameRateDouble() {
    val builder = getBuilder()
    builder.setVideoFrameRate(1.0 / 60.0)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-r", "1/60")))
  }

  @Test
  fun testSetVideoFrameRateFraction() {
    val builder = getBuilder()
    builder.setVideoFrameRate(Fraction.ONE_THIRD)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-r", "1/3")))
  }

  @Test
  fun testSetVideoFrameRateFramesPer() {
    val builder = getBuilder()
    builder.setVideoFrameRate(30, 1)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-r", "30/1")))
  }

  @Test
  fun testSetVideoWidth() {
    val builder = getBuilder()
    builder.setVideoWidth(1920)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(emptyList()))
  }

  @Test
  fun testSetVideoHeight() {
    val builder = getBuilder()
    builder.setVideoHeight(1080)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(emptyList()))
  }

  @Test
  fun testSetVideoWidthAndHeight() {
    val builder = getBuilder()
    builder.setVideoWidth(1920).setVideoHeight(1080)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-s", "1920x1080")))
  }

  @Test
  fun testSetVideoSize() {
    val builder = getBuilder()
    builder.setVideoResolution("1920x1080")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-s", "1920x1080")))
  }

  @Test
  fun testSetVideoMovflags() {
    val builder = getBuilder()
    builder.setVideoMovFlags("mov")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-movflags", "mov")))
  }

  @Test
  fun testSetVideoFrames() {
    val builder = getBuilder()
    builder.setFrames(30)
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-vframes", "30")))
  }

  @Test
  fun testSetVideoPixelFormat() {
    val builder = getBuilder()
    builder.setVideoPixelFormat("yuv420")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-pix_fmt", "yuv420")))
  }

  @Test
  fun testDisableSubtitle() {
    val builder = getBuilder()
    builder.disableSubtitle()
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-sn")))
  }

  @Test
  fun testSetSubtitlePreset() {
    val builder = getBuilder()
    builder.setSubtitlePreset("ac")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-spre", "ac")))
  }

  @Test
  fun testSetSubtitleCodec() {
    val builder = getBuilder()
    builder.setSubtitleCodec("vtt")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-scodec", "vtt")))
  }

  @Test
  fun testSetPreset() {
    val builder = getBuilder()
    builder.setPreset("pre")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-preset", "pre")))
  }

  @Test
  fun testSetPresetFilename() {
    val builder = getBuilder()
    builder.setPresetFilename("pre.txt")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-fpre", "pre.txt")))
  }

  @Test
  fun testSetStrict() {
    val builder = getBuilder()
    builder.setStrict(Strict.Strict)
    val command = builder.build(0)

    assertEquals("strict", command[command.indexOf("-strict") + 1])
  }

  @Test
  fun testAddExtraArgs() {
    val builder = getBuilder()
    builder.addExtraArgs("-some", "args")
    val command = builder.build(0)

    assertThat(removeCommon(command), `is`(listOf("-some", "args")))
  }
}
