package net.bramp.ffmpeg.builder

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.FFmpeg.Companion.AUDIO_FORMAT_S16
import net.bramp.ffmpeg.FFmpeg.Companion.AUDIO_SAMPLE_48000
import net.bramp.ffmpeg.FFmpeg.Companion.FPS_30
import net.bramp.ffmpeg.builder.FFmpegBuilder.Verbosity
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.chapter
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.program
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.stream
import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.tag
import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.usable
import net.bramp.ffmpeg.builder.StreamSpecifierType.Audio
import net.bramp.ffmpeg.builder.StreamSpecifierType.Subtitle
import net.bramp.ffmpeg.builder.StreamSpecifierType.Video
import net.bramp.ffmpeg.options.AudioEncodingOptions
import net.bramp.ffmpeg.options.MainEncodingOptions
import net.bramp.ffmpeg.options.VideoEncodingOptions
import org.junit.Test
import java.net.URI
import java.util.concurrent.TimeUnit

@Suppress("unused")
class FFmpegBuilderTest {
  @Test
  fun testNormal() {
    val args = FFmpegBuilder()
      .setVerbosity(Verbosity.Debug)
      .setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36",
      )
      .setInput("input")
      .setStartOffset(1500, TimeUnit.MILLISECONDS)
      .done()
      .overrideOutputFiles(true)
      .addOutput("output")
      .setFormat("mp4")
      .setStartOffset(500, TimeUnit.MILLISECONDS)
      .setAudioCodec("aac")
      .setAudioChannels(1)
      .setAudioSampleRate(48_000)
      .setAudioBitStreamFilter("bar")
      .setAudioQuality(1.0)
      .setVideoCodec("libx264")
      .setVideoFrameRate(FPS_30)
      .setVideoResolution(320, 240)
      .setVideoBitStreamFilter("foo")
      .setVideoQuality(2.0)
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "debug",
      "-user_agent",
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36",
      "-ss",
      "00:00:01.5",
      "-i",
      "input",
      "-f",
      "mp4",
      "-ss",
      "00:00:00.5",
      "-vcodec",
      "libx264",
      "-s",
      "320x240",
      "-r",
      "30/1",
      "-qscale:v",
      "2",
      "-bsf:v",
      "foo",
      "-acodec",
      "aac",
      "-ac",
      "1",
      "-ar",
      "48000",
      "-qscale:a",
      "1",
      "-bsf:a",
      "bar",
      "output",
    )
  }

  @Test
  fun testDisabled() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput("output")
      .disableAudio()
      .disableSubtitle()
      .disableVideo()
      .done()
      .build()

    args shouldBe listOf("-y", "-v", "error", "-i", "input", "-vn", "-an", "-sn", "output")
  }

  @Test
  fun testFilter() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput("output")
      .disableAudio()
      .disableSubtitle()
      .setVideoFilter("scale='trunc(ow/a/2)*2:320'")
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-i",
      "input",
      "-vf",
      "scale='trunc(ow/a/2)*2:320'",
      "-an",
      "-sn",
      "output",
    )
  }

  @Test
  fun testFilterAndScale() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput("output")
      .setVideoResolution(320, 240)
      .setVideoFilter("scale='trunc(ow/a/2)*2:320'")
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-i",
      "input",
      "-s",
      "320x240",
      "-vf",
      "scale='trunc(ow/a/2)*2:320'",
      "output",
    )
  }

  /** Tests if all the various encoding options actually get stored and used correctly */
  @Test
  fun testSetOptions() {
    val main = MainEncodingOptions("mp4", 1500L, 2L)
    val audio = AudioEncodingOptions(true, "aac", 1, AUDIO_SAMPLE_48000, AUDIO_FORMAT_S16, 1, 2.0)
    val video = VideoEncodingOptions(true, "libx264", FPS_30, 320, 240, 1, null, null, null)

    val options = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput("output")
      .useOptions(main)
      .useOptions(audio)
      .useOptions(video)
      .buildOptions()

    options.main shouldBe main
    options.audio shouldBe audio
    options.video shouldBe video
  }

  /** Tests if all the various encoding options actually get stored and used correctly */
  @Test
  fun testVideoCodecWithEnum() {
    val main = MainEncodingOptions("mp4", 1500L, 2L)
    val audio = AudioEncodingOptions(
      true,
      AudioCodec.AAC,
      1,
      AUDIO_SAMPLE_48000,
      AUDIO_FORMAT_S16,
      1,
      2.0,
    )
    val video = VideoEncodingOptions(true, VideoCodec.H264, FPS_30, 320, 240, 1, null, null, null)

    val options = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput("output")
      .useOptions(main)
      .useOptions(audio)
      .useOptions(video)
      .buildOptions()

    options.main shouldBe main
    options.audio shouldBe audio
    options.video shouldBe video
  }

  @Test
  fun testMultipleOutputs() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput("output1")
      .setVideoResolution(320, 240)
      .done()
      .addOutput("output2")
      .setVideoResolution(640, 480)
      .done()
      .addOutput("output3")
      .setVideoResolution("ntsc")
      .done()
      .build()

    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input", "-s", "320x240", "output1", "-s", "640x480",
      "output2", "-s", "ntsc", "output3",
    )
  }

  @Test
  fun testConflictingVideoSize() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder()
        .setInput("input")
        .done()
        .addOutput("output")
        .setVideoResolution(320, 240)
        .setVideoResolution("ntsc")
        .done()
        .build()
    }
  }

  @Test
  fun testURIOutput() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput(URI.create("udp://10.1.0.102:1234"))
      .setVideoResolution(320, 240)
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-i",
      "input",
      "-s",
      "320x240",
      "udp://10.1.0.102:1234",
    )
  }

  @Test
  fun testURIAndFilenameOutput() {
    shouldThrow<IllegalStateException> {
      FFmpegBuilder()
        .setInput("input")
        .done()
        .addOutput(URI.create("udp://10.1.0.102:1234"))
        .setFilename("filename")
        .done()
        .build()
    }
  }

  @Test
  fun testAddEmptyFilename() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder().setInput("input").done().addOutput("").done().build()
    }
  }

  @Test
  fun testSetEmptyFilename() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder()
        .setInput("input")
        .done()
        .addOutput("output")
        .setFilename("")
        .done()
        .build()
    }
  }

  @Test
  fun testMetaTags() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput("output")
      .addMetaTag("comment", "My Comment")
      .addMetaTag("title", "\"Video\"")
      .addMetaTag("author", "a=b:c")
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-i",
      "input",
      "-metadata",
      "comment=My Comment",
      "-metadata",
      "title=\"Video\"",
      "-metadata",
      "author=a=b:c",
      "output",
    )
  }

  @Test
  fun testMetaTagsWithSpecifier() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addOutput("output")
      .addMetaTag("title", "Movie Title")
      .addMetaTag(chapter(0), "author", "Bob")
      .addMetaTag(program(0), "comment", "Awesome")
      .addMetaTag(stream(0), "copyright", "Megacorp")
      .addMetaTag(stream(Video), "framerate", "24fps")
      .addMetaTag(stream(Video, 0), "artist", "Joe")
      .addMetaTag(stream(Audio, 0), "language", "eng")
      .addMetaTag(stream(Subtitle, 0), "language", "fre")
      .addMetaTag(stream(usable()), "year", "2010")
      .addMetaTag(stream(tag("key")), "a", "b")
      .addMetaTag(stream(tag("key", "value")), "a", "b")
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-i",
      "input",
      "-metadata",
      "title=Movie Title",
      "-metadata:c:0",
      "author=Bob",
      "-metadata:p:0",
      "comment=Awesome",
      "-metadata:s:0",
      "copyright=Megacorp",
      "-metadata:s:v",
      "framerate=24fps",
      "-metadata:s:v:0",
      "artist=Joe",
      "-metadata:s:a:0",
      "language=eng",
      "-metadata:s:s:0",
      "language=fre",
      "-metadata:s:u",
      "year=2010",
      "-metadata:s:m:key",
      "a=b",
      "-metadata:s:m:key:value",
      "a=b",
      "output",
    )
  }

  @Test
  fun testExtraArgs() {
    val args = FFmpegBuilder()
      .addExtraArgs("-a", "b")
      .setInput("input")
      .done()
      .addOutput("output")
      .addExtraArgs("-c", "d")
      .disableAudio()
      .disableSubtitle()
      .done()
      .build()

    args shouldBe listOf(
      "-y", "-v", "error", "-a", "b", "-i", "input", "-an", "-sn", "-c", "d", "output",
    )
  }

  @Test
  fun testVbr() {
    val args = FFmpegBuilder().setInput("input").done().setVBR(2).addOutput("output").done().build()

    args shouldBe listOf("-y", "-v", "error", "-i", "input", "-qscale:a", "2", "output")
  }

  @Test
  fun testVbrNegativeParam() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder().setInput("input").done().setVBR(-3).addOutput("output").done().build()
    }
  }

  @Test
  fun testVbrQualityExceedsRange() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder().setInput("input").done().setVBR(10).addOutput("output").done().build()
    }
  }

  @Test
  fun testNothing() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder().build()
    }
  }

  @Test
  fun testMultipleInput() {
    val args = FFmpegBuilder()
      .addInput("input1")
      .done()
      .addInput("input2")
      .done()
      .addOutput("output")
      .done()
      .build()

    args shouldBe listOf("-y", "-v", "error", "-i", "input1", "-i", "input2", "output")
  }

  @Test
  fun testAlternativeBuilderPattern() {
    val args = FFmpegBuilder()
      .addInput("input")
      .done()
      .addOutput(FFmpegOutputBuilder().setFilename("output.mp4").setVideoCodec("libx264"))
      .addOutput(FFmpegOutputBuilder().setFilename("output.flv").setVideoCodec("flv"))
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-i",
      "input",
      "-vcodec",
      "libx264",
      "output.mp4",
      "-vcodec",
      "flv",
      "output.flv",
    )
  }

  @Test
  fun testPresets() {
    val args = FFmpegBuilder()
      .addInput("input")
      .done()
      .addOutput("output")
      .setPreset("a")
      .setPresetFilename("b")
      .setVideoPreset("c")
      .setAudioPreset("d")
      .setSubtitlePreset("e")
      .done()
      .build()

    args shouldBe listOf(
      "-y", "-v", "error", "-i", "input", "-preset", "a", "-fpre", "b", "-vpre", "c", "-apre",
      "d", "-spre", "e", "output",
    )
  }

  @Test
  fun testThreads() {
    val args = FFmpegBuilder()
      .setThreads(2)
      .addInput("input")
      .done()
      .addOutput("output")
      .done()
      .build()

    args shouldBe listOf("-y", "-v", "error", "-threads", "2", "-i", "input", "output")
  }

  @Test
  fun testSetLoop() {
    val args = FFmpegBuilder()
      .addInput("input")
      .setStreamLoop(2)
      .done()
      .addOutput("output")
      .done()
      .build()

    args shouldBe listOf("-y", "-v", "error", "-stream_loop", "2", "-i", "input", "output")
  }

  @Test
  fun testZeroThreads() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder().setThreads(0)
    }
  }

  @Test
  fun testNegativeNumberOfThreads() {
    shouldThrow<IllegalArgumentException> {
      FFmpegBuilder().setThreads(-1)
    }
  }

  @Test
  fun testQuestion156() {
    val args = FFmpegBuilder()
      .overrideOutputFiles(true)
      .setVerbosity(Verbosity.Info)
      // X11 screen input
      .addInput(":0.0+0,0")
      .setFormat("x11grab")
      .setVideoResolution("1280x720")
      .setVideoFrameRate(30.0)
      .addExtraArgs("-draw_mouse", "0")
      .addExtraArgs("-thread_queue_size", "4096")
      .done()
      // alsa audio input
      .addInput("hw:0,1,0")
      .setFormat("alsa")
      .addExtraArgs("-thread_queue_size", "4096")
      .done()
      // Youtube output
      .addOutput("rtmp://a.rtmp.youtube.com/live2/XXX")
      .setAudioCodec("aac")
      .setFormat("flv")
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "info",
      "-f",
      "x11grab",
      "-s",
      "1280x720",
      "-r",
      "30/1",
      "-draw_mouse",
      "0",
      "-thread_queue_size",
      "4096",
      "-i",
      ":0.0+0,0",
      "-f",
      "alsa",
      "-thread_queue_size",
      "4096",
      "-i",
      "hw:0,1,0",
      "-f",
      "flv",
      "-acodec",
      "aac",
      "rtmp://a.rtmp.youtube.com/live2/XXX",
    )
  }

  @Test
  fun testSetStrict() {
    val args = FFmpegBuilder()
      .addInput("input.mp4")
      .done()
      .addOutput("output.mp4")
      .done()
      .setStrict(Strict.Experimental)
      .build()

    args shouldBe listOf(
      "-strict",
      "experimental",
      "-y",
      "-v",
      "error",
      "-i",
      "input.mp4",
      "output.mp4",
    )
  }

  @Test
  fun testQuestion65() {
    val args = FFmpegBuilder()
      .addInput("aevalsrc=0")
      .setFormat("lavfi")
      .done()
      .addInput("1.mp4")
      .done()
      .addOutput("output.mp4")
      .setVideoCodec("copy")
      .setAudioCodec("aac")
      .addExtraArgs("-map", "0:0")
      .addExtraArgs("-map", "1:0")
      .addExtraArgs("-shortest")
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-f",
      "lavfi",
      "-i",
      "aevalsrc=0",
      "-i",
      "1.mp4",
      "-vcodec",
      "copy",
      "-acodec",
      "aac",
      "-map",
      "0:0",
      "-map",
      "1:0",
      "-shortest",
      "output.mp4",
    )
  }

  @Test
  fun testQuestion295() {
    val args = FFmpegBuilder()
      .addInput("audio=<device>")
      .setFormat("dshow")
      .done()
      .addInput("desktop")
      .setFormat("gdigrab")
      .setFrames(30)
      .done()
      .addOutput("video_file_name.mp4")
      .setVideoCodec("libx264")
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-f",
      "dshow",
      "-i",
      "audio=<device>",
      "-f",
      "gdigrab",
      "-vframes",
      "30",
      "-i",
      "desktop",
      "-vcodec",
      "libx264",
      "video_file_name.mp4",
    )
  }

  @Test
  fun testQuestion252() {
    val args = FFmpegBuilder()
      .addInput("video_160x90_250k.webm")
      .setFormat("webm_dash_manifest")
      .done()
      .addInput("video_320x180_500k.webm")
      .setFormat("webm_dash_manifest")
      .done()
      .addInput("video_640x360_750k.webm")
      .setFormat("webm_dash_manifest")
      .done()
      .addInput("video_640x360_1000k.webm")
      .setFormat("webm_dash_manifest")
      .done()
      .addInput("video_1280x720_600k.webm")
      .setFormat("webm_dash_manifest")
      .done()
      .addInput("audio_128k.webm")
      .setFormat("webm_dash_manifest")
      .done()
      .addOutput("manifest.mp4")
      .setVideoCodec("copy")
      .setAudioCodec("copy")
      .addExtraArgs("-map", "0")
      .addExtraArgs("-map", "1")
      .addExtraArgs("-map", "2")
      .addExtraArgs("-map", "3")
      .addExtraArgs("-map", "4")
      .addExtraArgs("-map", "5")
      .setFormat("webm_dash_manifest")
      .addExtraArgs("-adaptation_sets", "id=0,streams=0,1,2,3,4 id=1,streams=5")
      .done()
      .build()

    args shouldBe listOf(
      "-y",
      "-v",
      "error",
      "-f",
      "webm_dash_manifest",
      "-i",
      "video_160x90_250k.webm",
      "-f",
      "webm_dash_manifest",
      "-i",
      "video_320x180_500k.webm",
      "-f",
      "webm_dash_manifest",
      "-i",
      "video_640x360_750k.webm",
      "-f",
      "webm_dash_manifest",
      "-i",
      "video_640x360_1000k.webm",
      "-f",
      "webm_dash_manifest",
      "-i",
      "video_1280x720_600k.webm",
      "-f",
      "webm_dash_manifest",
      "-i",
      "audio_128k.webm",
      "-f",
      "webm_dash_manifest",
      "-vcodec",
      "copy",
      "-acodec",
      "copy",
      "-map",
      "0",
      "-map",
      "1",
      "-map",
      "2",
      "-map",
      "3",
      "-map",
      "4",
      "-map",
      "5",
      "-adaptation_sets",
      "id=0,streams=0,1,2,3,4 id=1,streams=5",
      "manifest.mp4",
    )
  }
}
