package net.bramp.ffmpeg

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.bramp.ffmpeg.fixtures.channelLayouts
import net.bramp.ffmpeg.fixtures.codecs
import net.bramp.ffmpeg.fixtures.filters as fixtureFilters
import net.bramp.ffmpeg.fixtures.formats
import net.bramp.ffmpeg.fixtures.pixelFormats
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.lang.MockProcess
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class FFmpegTest {
  private lateinit var runFunc: ProcessFunction
  private lateinit var ffmpeg: FFmpeg

  @Before
  fun before() {
    runFunc = mockk()

    val path = FFmpeg.DEFAULT_PATH
    every { runFunc.run(listOf(path, "-version")) } returns MockProcess(Helper.loadResource("ffmpeg-version"))
    every { runFunc.run(listOf(path, "-formats")) } returns MockProcess(Helper.loadResource("ffmpeg-formats"))
    every { runFunc.run(listOf(path, "-codecs")) } returns MockProcess(Helper.loadResource("ffmpeg-codecs"))
    every { runFunc.run(listOf(path, "-pix_fmts")) } returns MockProcess(Helper.loadResource("ffmpeg-pix_fmts"))
    every { runFunc.run(match { list -> list.any { it.contains("toto.mp4") } }) } returns MockProcess(
      null,
      Helper.loadResource("ffmpeg-version"),
      Helper.loadResource("ffmpeg-no-such-file"),
    )
    every { runFunc.run(listOf(path, "-filters")) } returns MockProcess(Helper.loadResource("ffmpeg-filters"))
    every { runFunc.run(listOf(path, "-layouts")) } returns MockProcess(Helper.loadResource("ffmpeg-layouts"))

    ffmpeg = FFmpeg(FFmpeg.DEFAULT_PATH, runFunc)
  }

  @Test
  fun testVersion() {
    assertEquals("ffmpeg version 0.10.9-7:0.10.9-1~raring1", ffmpeg.version())
    assertEquals("ffmpeg version 0.10.9-7:0.10.9-1~raring1", ffmpeg.version())

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-version")) }
  }

  @Test
  fun testStartOffsetOption() {
    val localFfmpeg = FFmpeg()

    val builder = localFfmpeg.builder()
      .addInput(Samples.big_buck_bunny_720p_1mb)
      .setStartOffset(1, TimeUnit.SECONDS)
      .done()
      .addOutput(Samples.output_mp4)
      .done()

    try {
      localFfmpeg.runWithBuilder(builder)
    }
    catch(t: Throwable) {
      fail("${t.javaClass.simpleName} was thrown")
    }
  }

  @Test
  fun testDurationOption() {
    val localFfmpeg = FFmpeg()

    val builder = localFfmpeg.builder()
      .addInput(Samples.big_buck_bunny_720p_1mb)
      .setDuration(1, TimeUnit.SECONDS)
      .done()
      .addOutput(Samples.output_mp4)
      .done()

    try {
      localFfmpeg.runWithBuilder(builder)
    }
    catch(t: Throwable) {
      fail("${t.javaClass.simpleName} was thrown")
    }
  }

  @Test
  fun testCodecs() {
    // Run twice, the second should be cached
    assertEquals(codecs, ffmpeg.codecs())
    assertEquals(codecs, ffmpeg.codecs())

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-codecs")) }
  }

  @Test
  fun testFormats() {
    // Run twice, the second should be cached
    assertEquals(formats, ffmpeg.formats())
    assertEquals(formats, ffmpeg.formats())

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-formats")) }
  }

  @Test
  fun testReadProcessStreams() {
    // process input stream
    val processInputStream = mockk<Appendable>(relaxed = true)
    ffmpeg.processOutputStream = processInputStream
    // process error stream
    val processErrStream = mockk<Appendable>(relaxed = true)
    ffmpeg.processErrorStream = processErrStream
    // run ffmpeg with non existing file
    ffmpeg.run(listOf("-i", "toto.mp4"))
    // check calls to Appendables
    verify(exactly = 1) { processInputStream.append(any<CharSequence>()) }
    verify(exactly = 1) { processErrStream.append(any<CharSequence>()) }
  }

  @Test
  fun testPixelFormat() {
    // Run twice, the second should be cached
    assertEquals(pixelFormats, ffmpeg.pixelFormats())
    assertEquals(pixelFormats, ffmpeg.pixelFormats())

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-pix_fmts")) }
  }

  @Test
  fun testFilters() {
    // Run twice, the second should be cached
    val filters = ffmpeg.filters()

    for(i in filters!!.indices) {
      assertEquals(fixtureFilters[i], filters[i])
    }

    assertEquals(fixtureFilters, ffmpeg.filters())
    assertEquals(fixtureFilters, ffmpeg.filters())

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-filters")) }
  }

  @Test
  fun testLayouts() {
    assertEquals(channelLayouts, ffmpeg.channelLayouts())
    assertEquals(channelLayouts, ffmpeg.channelLayouts())

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-layouts")) }
  }
}
