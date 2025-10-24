package net.bramp.ffmpeg

import com.google.common.base.Joiner
import io.mockk.every
import io.mockk.mockk
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.lang.MockProcess
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Ensures the examples in the Examples on github continue to work.
 * https://github.com/bramp/ffmpeg-cli-wrapper/wiki/Random-Examples
 */
class ExamplesTest {

  lateinit var runFunc: ProcessFunction
  lateinit var ffmpeg: FFmpeg

  @Before
  fun before() {
    runFunc = mockk()
    every { runFunc.run(any()) } answers { MockProcess(Helper.loadResource("ffmpeg-version") as InputStream) }
    ffmpeg = FFmpeg("ffmpeg", runFunc)
  }

  @Test
  fun testExample1() {
    ffmpeg = FFmpeg("ffmpeg\\win64\\bin\\ffmpeg.exe", runFunc)

    val builder = FFmpegBuilder()
      .addExtraArgs("-rtbufsize", "1500M")
      .addExtraArgs("-re")
      .setInput("video=\"Microsoft Camera Rear\":audio=\"Microphone Array (Realtek High Definition Audio(SST))\"")
      .setFormat("dshow")
      .done()
      .addOutput("rtmp://a.rtmp.youtube.com/live2/1234-5678")
      .setFormat("flv")
      .addExtraArgs("-bufsize", "4000k")
      .addExtraArgs("-maxrate", "1000k")
      .setAudioCodec("libmp3lame")
      .setAudioSampleRate(FFmpeg.AUDIO_SAMPLE_44100)
      .setAudioBitRate(1_000_000)
      .addExtraArgs("-profile:v", "baseline")
      .setVideoCodec("libx264")
      .setVideoPixelFormat("yuv420p")
      .setVideoResolution(426, 240)
      .setVideoBitRate(2_000_000)
      .setVideoFrameRate(30.0)
      .addExtraArgs("-deinterlace")
      .addExtraArgs("-preset", "medium")
      .addExtraArgs("-g", "30")
      .done()

    val expected =
      "ffmpeg\\win64\\bin\\ffmpeg.exe -y -v error" +
        " -rtbufsize 1500M -re -f dshow" +
        " -i video=\"Microsoft Camera Rear\":audio=\"Microphone Array (Realtek High Definition Audio(SST))\"" +
        " -f flv" +
        " -vcodec libx264 -pix_fmt yuv420p -s 426x240 -r 30/1 -b:v 2000000" +
        " -acodec libmp3lame -ar 44100 -b:a 1000000 -bufsize 4000k -maxrate 1000k" +
        " -profile:v baseline -deinterlace -preset medium -g 30" +
        " rtmp://a.rtmp.youtube.com/live2/1234-5678"

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))

    assertEquals(expected, actual)
  }

  @Test
  fun testExample2() {
    val builder = FFmpegBuilder()
      .setInput("input.mkv")
      .done()
      .addOutput("output.ogv")
      .setVideoCodec("libtheora")
      .addExtraArgs("-qscale:v", "7")
      .setAudioCodec("libvorbis")
      .addExtraArgs("-qscale:a", "5")
      .done()

    val expected =
      "ffmpeg -y -v error" +
        " -i input.mkv" +
        " -vcodec libtheora" +
        " -acodec libvorbis" +
        " -qscale:v 7" +
        " -qscale:a 5" +
        " output.ogv"

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }

  @Test
  fun testExample3() {
    val builder = FFmpegBuilder()
      .setInput("sample.avi")
      .done()
      .addOutput("thumbnail.png")
      .setFrames(1)
      .setVideoFilter("select='gte(n\\,10)',scale=200:-1")
      .done()

    val expected =
      "ffmpeg -y -v error" +
        " -i sample.avi" +
        " -vframes 1 -vf select='gte(n\\,10)',scale=200:-1" +
        " thumbnail.png"

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }

  // Read from RTSP (IP camera)
  @Test
  fun testExample4() {
    val builder = FFmpegBuilder()
      .setInput("rtsp://192.168.1.1:1234/")
      .done()
      .addOutput("img%03d.jpg")
      .setFormat("image2")
      .done()

    val expected = "ffmpeg -y -v error -i rtsp://192.168.1.1:1234/ -f image2 img%03d.jpg"

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }

  // Set the working directory of ffmpeg
  @Ignore("because this test will invoke /path/to/ffmpeg.")
  @Test
  fun testExample5() {
    // Note: This is intentionally left as a placeholder per the Java test (it invokes ffmpeg)
  }

  // Create a video from images
  @Test
  fun testExample6() {
    val builder = FFmpegBuilder()
      .addInput("image%03d.png")
      .done()
      .addOutput("output.mp4")
      .setVideoFrameRate(FFmpeg.FPS_24)
      .done()

    val expected = "ffmpeg -y -v error -i image%03d.png -r 24/1 output.mp4"

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }

  @Test
  fun testExample7() {
    val builder = FFmpegBuilder()
      .addInput("original.mp4")
      .done()
      .addInput("spot.mp4")
      .done()
      .addOutput("with-video.mp4")
      .setComplexFilter(
        "[1:v]scale=368:207,setpts=PTS-STARTPTS+5/TB [ov]; " +
          "[0:v][ov] overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2:enable='between(t,5,15)' [v]",
      )
      .addExtraArgs("-map", "[v]")
      .addExtraArgs("-map", "0:a")
      .setVideoCodec("libx264")
      .setPreset("ultrafast")
      .setConstantRateFactor(20.0)
      .setAudioCodec("copy")
      .addExtraArgs("-shortest")
      .done()

    val expected =
      "ffmpeg -y -v error" +
        " -i original.mp4" +
        " -i spot.mp4" +
        " -preset ultrafast" +
        " -crf 20" +
        " -filter_complex [1:v]scale=368:207,setpts=PTS-STARTPTS+5/TB [ov]; [0:v][ov] overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2:enable='between(t,5,15)' [v]" +
        " -vcodec libx264" +
        " -acodec copy" +
        " -map [v]" +
        " -map 0:a" +
        " -shortest" +
        " with-video.mp4"

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }

  // Transcode to iOS HEVC format, with video filter set before output
  @Test
  fun testExample8() {
    val builder = FFmpegBuilder()
      .addInput("original.mp4")
      .done()
      .setVideoFilter("select='gte(n\\,10)',scale=200:-1")
      .addOutput("hevc-video.mp4")
      .addExtraArgs("-tag:v", "hvc1")
      .setVideoCodec("libx265")
      .done()

    val expected =
      "ffmpeg -y -v error" +
        " -i original.mp4" +
        " -vf select='gte(n\\,10)',scale=200:-1" +
        " -vcodec libx265" +
        " -tag:v hvc1" +
        " hevc-video.mp4"

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }

  // Convert a stereo mp3 into two mono tracks.
  @Test
  fun testExample9() {
    val builder = FFmpegBuilder()
      .setVerbosity(FFmpegBuilder.Verbosity.Debug)
      .setInput("input.mp3")
      .done()
      .overrideOutputFiles(true)
      .addOutput("left.mp3")
      .addExtraArgs("-map_channel", "0.0.0")
      .done()
      .addOutput("right.mp3")
      .addExtraArgs("-map_channel", "0.0.1")
      .done()

    val expected =
      "ffmpeg -y -v debug " +
        "-i input.mp3 " +
        "-map_channel 0.0.0 left.mp3 " +
        "-map_channel 0.0.1 right.mp3"

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }

  // A test with videos added in a loop.
  @Test
  fun testExample10() {
    val expected =
      "ffmpeg -y -v error" +
        " -f webm_dash_manifest" +
        " -i audio.webm" +
        " -f webm_dash_manifest" +
        " -i video_1.webm" +
        " -f webm_dash_manifest" +
        " -i video_2.webm" +
        " -f webm_dash_manifest" +
        " -i video_3.webm" +
        " -vcodec copy -acodec copy" +
        " -map 0 -map 1 -map 2 -map 3" +
        " -adaptation_sets \"id=0,streams=0 id=1,streams=1,2,3\"" +
        " output.mpd"

    val streams = mutableListOf<String>()
    val builder = FFmpegBuilder()
    builder.addInput("audio.webm").setFormat("webm_dash_manifest")
    for(i in 1..3) {
      builder.addInput("video_$i.webm").setFormat("webm_dash_manifest")
      streams.add("$i")
    }
    val out = builder
      .addOutput("output.mpd")
      .setVideoCodec("copy")
      .setAudioCodec("copy")
      .addExtraArgs("-map", "0")
    for(stream in streams) {
      out.addExtraArgs("-map", stream)
    }
    out.addExtraArgs("-adaptation_sets", "\"id=0,streams=0 id=1,streams=" + streams.joinToString(",") + "\"").done()

    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }

  // Directly use a Process instead of a FFmpegJob
  @Test
  @Ignore("because this test will invoke /path/to/ffmpeg.")
  fun testExample11() {
    // intentionally left blank (would invoke ffmpeg)
  }

  @Test
  fun testExampleExample() {
    val builder = FFmpegBuilder()
      .setInput("input.mp4")
      .setStartOffset(1, TimeUnit.MINUTES)
      .done()
      .addOutput("output.mp4")
      .setDuration(1, TimeUnit.MINUTES)
      .setVideoCodec("copy")
      .setAudioCodec("copy")
      .done()

    val expected =
      "ffmpeg -y -v error -ss 00:01:00 -i input.mp4 -t 00:01:00 -vcodec copy -acodec copy output.mp4"
    val actual = Joiner.on(" ").join(ffmpeg.path(builder.build()))
    assertEquals(expected, actual)
  }
}
