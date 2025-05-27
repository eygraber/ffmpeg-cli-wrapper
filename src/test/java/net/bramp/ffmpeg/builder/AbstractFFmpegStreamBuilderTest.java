package net.bramp.ffmpeg.builder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.math.Fraction;
import org.junit.Test;

public abstract class AbstractFFmpegStreamBuilderTest {
  protected abstract AbstractFFmpegStreamBuilder<?> getBuilder();

  protected abstract List<String> removeCommon(List<String> command);

  @Test
  public void testSetFormat() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setFormat("mp4");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-f", "mp4")));
  }

  @Test
  public void testSetStartOffset() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setStartOffset(10, TimeUnit.SECONDS);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-ss", "00:00:10")));
  }

  @Test
  public void testSetDuration() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setDuration(5, TimeUnit.SECONDS);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-t", "00:00:05")));
  }

  @Test
  public void testAddMetaTagKeyValue() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.addMetaTag("key", "value");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-metadata", "key=value")));
  }

  @Test
  public void testAddMetaTagSpecKeyValue() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.addMetaTag(MetadataSpecifier.Companion.stream(1), "key", "value");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-metadata:s:1", "key=value")));
  }

  @Test
  public void testDisableAudio() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.disableAudio();
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-an")));
  }

  @Test
  public void testSetAudioCodec() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setAudioCodec("acc");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-acodec", "acc")));
  }

  @Test
  public void testSetAudioChannels() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setAudioChannels(7);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-ac", "7")));
  }

  @Test
  public void testSetAudioSampleRate() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setAudioSampleRate(44100);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-ar", "44100")));
  }

  @Test
  public void testSetAudioPreset() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setAudioPreset("ac");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-apre", "ac")));
  }

  @Test
  public void testDisableVideo() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.disableVideo();
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-vn")));
  }

  @Test
  public void testSetVideoCodec() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoCodec("libx264");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-vcodec", "libx264")));
  }

  @Test
  public void testSetVideoCopyInkf() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoCopyInkf(true);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-copyinkf")));
  }

  @Test
  public void testSetVideoFrameRateDouble() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoFrameRate(1d / 60d);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-r", "1/60")));
  }

  @Test
  public void testSetVideoFrameRateFraction() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoFrameRate(Fraction.ONE_THIRD);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-r", "1/3")));
  }

  @Test
  public void testSetVideoFrameRateFramesPer() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoFrameRate(30, 1);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-r", "30/1")));
  }

  @Test
  public void testSetVideoWidth() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoWidth(1920);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of()));
  }

  @Test
  public void testSetVideoHeight() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoHeight(1080);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of()));
  }

  @Test
  public void testSetVideoWidthAndHeight() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoWidth(1920).setVideoHeight(1080);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-s", "1920x1080")));
  }

  @Test
  public void testSetVideoSize() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoResolution("1920x1080");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-s", "1920x1080")));
  }

  @Test
  public void testSetVideoMovflags() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoMovFlags("mov");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-movflags", "mov")));
  }

  @Test
  public void testSetVideoFrames() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setFrames(30);
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-vframes", "30")));
  }

  @Test
  public void testSetVideoPixelFormat() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setVideoPixelFormat("yuv420");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-pix_fmt", "yuv420")));
  }

  @Test
  public void testDisableSubtitle() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.disableSubtitle();
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-sn")));
  }

  @Test
  public void testSetSubtitlePreset() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setSubtitlePreset("ac");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-spre", "ac")));
  }

  @Test
  public void testSetSubtitleCodec() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setSubtitleCodec("vtt");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-scodec", "vtt")));
  }

  @Test
  public void testSetPreset() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setPreset("pre");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-preset", "pre")));
  }

  @Test
  public void testSetPresetFilename() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setPresetFilename("pre.txt");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-fpre", "pre.txt")));
  }

  @Test
  public void testSetStrict() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.setStrict(Strict.Strict);
    List<String> command = builder.build(0);

    assertEquals("strict", command.get(command.indexOf("-strict") + 1));
  }

  @Test
  public void testAddExtraArgs() {
    AbstractFFmpegStreamBuilder<?> builder = getBuilder();
    builder.addExtraArgs("-some", "args");
    List<String> command = builder.build(0);

    assertThat(removeCommon(command), is(ImmutableList.of("-some", "args")));
  }
}
