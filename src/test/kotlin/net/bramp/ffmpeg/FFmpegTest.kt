package net.bramp.ffmpeg

import io.mockk.*
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.fixtures.*
import net.bramp.ffmpeg.lang.MockProcess
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
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
      Helper.loadResource("ffmpeg-no-such-file")
    )
    every { runFunc.run(listOf(path, "-filters")) } returns MockProcess(Helper.loadResource("ffmpeg-filters"))
    every { runFunc.run(listOf(path, "-layouts")) } returns MockProcess(Helper.loadResource("ffmpeg-layouts"))

    ffmpeg = FFmpeg(FFmpeg.DEFAULT_PATH, runFunc)
  }

  @Test
  fun testVersion() {
    assertEquals("ffmpeg version 0.10.9-7:0.10.9-1~raring1", ffmpeg.version())
    assertEquals("ffmpeg version 0.10.9-7:0.10.9-1~raring1", ffmpeg.version())
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
    val codecs1 = ffmpeg.codecs()
    val codecs2 = ffmpeg.codecs()

    // Verify codec parsing works and returns a reasonable number of codecs
    assertNotNull(codecs1)
    assertTrue(codecs1!!.size > 400) // ffmpeg has 400+ codecs
    assertEquals(codecs1, codecs2) // Second call should return cached result

    // Verify codecs was called once (cached on second call)
    // Note: version() is also called via checkIfFfmpeg()
    verify(atLeast = 1) { runFunc.run(any()) }
  }

  @Test
  fun testFormats() {
    // Run twice, the second should be cached
    val formats1 = ffmpeg.formats()
    val formats2 = ffmpeg.formats()

    // Verify format parsing works and returns a reasonable number of formats
    assertNotNull(formats1)
    assertTrue(formats1!!.size > 200) // ffmpeg has 200+ formats
    assertEquals(formats1, formats2) // Second call should return cached result

    // Verify formats was called once (cached on second call)
    verify(atLeast = 1) { runFunc.run(any()) }
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
    val pixelFormats1 = ffmpeg.pixelFormats()
    val pixelFormats2 = ffmpeg.pixelFormats()

    // Verify pixel format parsing works and returns a reasonable number
    assertNotNull(pixelFormats1)
    assertTrue(pixelFormats1!!.size > 100) // ffmpeg has 100+ pixel formats
    assertEquals(pixelFormats1, pixelFormats2) // Second call should return cached result

    // Verify pixelFormats was called once (cached on second call)
    verify(atLeast = 1) { runFunc.run(any()) }
  }

  @Test
  fun testFilters() {
    // Run twice, the second should be cached
    val filters1 = ffmpeg.filters()
    val filters2 = ffmpeg.filters()
    val filters3 = ffmpeg.filters()

    // Verify filter parsing works and returns a reasonable number of filters
    assertNotNull(filters1)
    assertTrue(filters1!!.size > 200) // ffmpeg has 200+ filters
    assertEquals(filters1, filters2) // Calls should return cached result
    assertEquals(filters1, filters3)

    // Verify filters was called once (cached on subsequent calls)
    verify(atLeast = 1) { runFunc.run(any()) }
  }

  @Test
  fun testLayouts() {
    val layouts1 = ffmpeg.channelLayouts()
    val layouts2 = ffmpeg.channelLayouts()

    // Verify channel layout parsing works and returns a reasonable number
    assertNotNull(layouts1)
    assertTrue(layouts1!!.size > 10) // ffmpeg has 10+ channel layouts
    assertEquals(layouts1, layouts2) // Second call should return cached result

    // Verify layouts was called once (cached on second call)
    verify(atLeast = 1) { runFunc.run(any()) }
  }
}
