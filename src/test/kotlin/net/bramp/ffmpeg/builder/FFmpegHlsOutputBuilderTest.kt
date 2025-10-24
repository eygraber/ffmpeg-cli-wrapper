package net.bramp.ffmpeg.builder

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.fixtures.Samples
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class FFmpegHlsOutputBuilderTest : AbstractFFmpegOutputBuilderTest() {

  override fun getBuilder(): AbstractFFmpegOutputBuilder<*> = FFmpegBuilder().addInput(
    "input.mp4",
  ).done().addHlsOutput("output.m3u8")

  override fun removeCommon(command: List<String>): List<String> {
    assertEquals("-f", command[0])
    assertEquals("hls", command[1])
    assertEquals("output.m3u8", command[command.size - 1])

    return command.subList(2, command.size - 1)
  }

  @Test
  fun testAddHlsOutput() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addHlsOutput("output.m3u8")
      .setHlsTime(5, TimeUnit.MILLISECONDS)
      .setHlsBaseUrl("test1234/")
      .setHlsListSize(3)
      .setHlsInitTime(3, TimeUnit.MILLISECONDS)
      .setHlsSegmentFileName("file%03d.ts")
      .done()
      .build()

    assertEquals(
      listOf(
        "-y",
        "-v",
        "error",
        "-i",
        "input",
        "-f",
        "hls",
        "-hls_time",
        "00:00:00.005",
        "-hls_segment_filename",
        "file%03d.ts",
        "-hls_init_time",
        "00:00:00.003",
        "-hls_list_size",
        "3",
        "-hls_base_url",
        "test1234/",
        "output.m3u8",
      ),
      args,
    )
  }

  @Test
  fun mixedHlsAndDefault() {
    val args = FFmpegBuilder()
      .setInput("input")
      .done()
      .addHlsOutput("output.m3u8")
      .setHlsTime(5, TimeUnit.MILLISECONDS)
      .setHlsBaseUrl("test1234/")
      .setVideoBitRate(3)
      .setVideoFilter("TEST")
      .setHlsListSize(3)
      .setHlsInitTime(3, TimeUnit.MILLISECONDS)
      .setHlsSegmentFileName("file%03d.ts")
      .done()
      .build()

    assertEquals(
      listOf(
        "-y",
        "-v",
        "error",
        "-i",
        "input",
        "-f",
        "hls",
        "-b:v",
        "3",
        "-vf",
        "TEST",
        "-hls_time",
        "00:00:00.005",
        "-hls_segment_filename",
        "file%03d.ts",
        "-hls_init_time",
        "00:00:00.003",
        "-hls_list_size",
        "3",
        "-hls_base_url",
        "test1234/",
        "output.m3u8",
      ),
      args,
    )
  }

  @Test
  @Throws(IOException::class)
  fun testConvertVideoToHls() {
    val manifestFilePath = Paths.get("tmp/output.m3u8")
    val segmentFilePath = Paths.get("tmp/file000.ts")

    Files.createDirectories(Paths.get("tmp/"))
    Files.deleteIfExists(manifestFilePath)
    Files.deleteIfExists(segmentFilePath)

    val command = FFmpegBuilder()
      .setInput(Samples.TEST_PREFIX + Samples.base_big_buck_bunny_720p_1mb)
      .done()
      .addHlsOutput("tmp/output.m3u8")
      .setHlsTime(5, TimeUnit.SECONDS)
      .setHlsBaseUrl("test1234/")
      .setVideoBitRate(1000)
      .setHlsListSize(3)
      .setHlsInitTime(3, TimeUnit.MILLISECONDS)
      .setHlsSegmentFileName("tmp/file%03d.ts")
      .done()
      .build()

    FFmpeg().run(command)

    assertTrue(Files.exists(manifestFilePath))
    assertTrue(Files.exists(segmentFilePath))
  }

  @Test
  @Throws(IOException::class)
  fun testConvertVideoToHlsSetHlsTime() {
    val manifestFilePath = Paths.get("tmp/output.m3u8")
    val segmentFilePath = Paths.get("tmp/file000.ts")

    Files.createDirectories(Paths.get("tmp/"))
    Files.deleteIfExists(manifestFilePath)
    Files.deleteIfExists(segmentFilePath)

    val command = FFmpegBuilder()
      .setInput(Samples.TEST_PREFIX + Samples.base_big_buck_bunny_720p_1mb)
      .done()
      .addHlsOutput("tmp/output.m3u8")
      .setHlsTime(5, TimeUnit.SECONDS)
      .setHlsSegmentFileName("tmp/file%03d.ts")
      .done()
      .build()

    FFmpeg().run(command)

    assertTrue(Files.exists(manifestFilePath))
    assertTrue(Files.exists(segmentFilePath))
  }

  @Test
  @Throws(IOException::class)
  fun testConvertVideoToHlsSetHlsInitTime() {
    val manifestFilePath = Paths.get("tmp/output.m3u8")
    val segmentFilePath = Paths.get("tmp/file000.ts")

    Files.createDirectories(Paths.get("tmp/"))
    Files.deleteIfExists(manifestFilePath)
    Files.deleteIfExists(segmentFilePath)

    val command = FFmpegBuilder()
      .setInput(Samples.TEST_PREFIX + Samples.base_big_buck_bunny_720p_1mb)
      .done()
      .addHlsOutput("tmp/output.m3u8")
      .setHlsInitTime(5, TimeUnit.SECONDS)
      .setHlsSegmentFileName("tmp/file%03d.ts")
      .done()
      .build()

    FFmpeg().run(command)

    assertTrue(Files.exists(manifestFilePath))
    assertTrue(Files.exists(segmentFilePath))
  }

  @Test
  fun testHlsTime() {
    val command = FFmpegHlsOutputBuilder(FFmpegBuilder(), "output.m3u8")
      .setHlsTime(5, TimeUnit.SECONDS)
      .build(0)

    assertThat(command, `is`(listOf("-f", "hls", "-hls_time", "00:00:05", "output.m3u8")))
  }

  @Test
  fun testHlsSegmentFileName() {
    val command = FFmpegHlsOutputBuilder(FFmpegBuilder(), "output.m3u8")
      .setHlsSegmentFileName("segment%03d.ts")
      .build(0)

    assertThat(
      command,
      `is`(
        listOf(
          "-f",
          "hls",
          "-hls_segment_filename",
          "segment%03d.ts",
          "output.m3u8",
        ),
      ),
    )
  }

  @Test
  fun testHlsInitTime() {
    val command = FFmpegHlsOutputBuilder(FFmpegBuilder(), "output.m3u8")
      .setHlsInitTime(10, TimeUnit.MILLISECONDS)
      .build(0)

    assertThat(
      command,
      `is`(listOf("-f", "hls", "-hls_init_time", "00:00:00.01", "output.m3u8")),
    )
  }

  @Test
  fun testHlsListSize() {
    val command = FFmpegHlsOutputBuilder(FFmpegBuilder(), "output.m3u8")
      .setHlsListSize(3)
      .build(0)

    assertThat(command, `is`(listOf("-f", "hls", "-hls_list_size", "3", "output.m3u8")))
  }

  @Test
  fun testHlsBaseUrl() {
    val command = FFmpegHlsOutputBuilder(FFmpegBuilder(), "output.m3u8")
      .setHlsBaseUrl("/base")
      .build(0)

    assertThat(command, `is`(listOf("-f", "hls", "-hls_base_url", "/base", "output.m3u8")))
  }

  @Test
  override fun testSetFormat() {
    val command = getBuilder().setFormat("hls").build(0)

    // removeCommon already asserts -f hls and removes that part. Therefore: Expecting no more elements
    assertEquals(removeCommon(command).size, 0)
  }
}
