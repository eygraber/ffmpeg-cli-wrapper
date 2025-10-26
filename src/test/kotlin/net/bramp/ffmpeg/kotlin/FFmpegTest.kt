package net.bramp.ffmpeg.kotlin

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.bramp.ffmpeg.kotlin.fixtures.Samples
import net.bramp.ffmpeg.kotlin.fixtures.channelLayouts
import net.bramp.ffmpeg.kotlin.fixtures.codecs
import net.bramp.ffmpeg.kotlin.fixtures.formats
import net.bramp.ffmpeg.kotlin.fixtures.pixelFormats
import net.bramp.ffmpeg.kotlin.lang.MockProcess
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
    ffmpeg.version() shouldBe "ffmpeg version 0.10.9-7:0.10.9-1~raring1"
    ffmpeg.version() shouldBe "ffmpeg version 0.10.9-7:0.10.9-1~raring1"

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

    shouldNotThrow<Throwable> {
      localFfmpeg.runWithBuilder(builder)
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

    shouldNotThrow<Throwable> {
      localFfmpeg.runWithBuilder(builder)
    }
  }

  @Test
  fun testCodecs() {
    // Run twice, the second should be cached
    ffmpeg.codecs() shouldBe codecs
    ffmpeg.codecs() shouldBe codecs

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-codecs")) }
  }

  @Test
  fun testFormats() {
    // Run twice, the second should be cached
    ffmpeg.formats() shouldBe formats
    ffmpeg.formats() shouldBe formats

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
    ffmpeg.pixelFormats() shouldBe pixelFormats
    ffmpeg.pixelFormats() shouldBe pixelFormats

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-pix_fmts")) }
  }

  @Test
  fun testFilters() {
    // Run twice, the second should be cached
    val filters = ffmpeg.filters()

    for(i in filters!!.indices) {
      filters[i] shouldBe net.bramp.ffmpeg.kotlin.fixtures.filters[i]
    }

    ffmpeg.filters() shouldBe net.bramp.ffmpeg.kotlin.fixtures.filters
    ffmpeg.filters() shouldBe net.bramp.ffmpeg.kotlin.fixtures.filters

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-filters")) }
  }

  @Test
  fun testLayouts() {
    ffmpeg.channelLayouts() shouldBe channelLayouts
    ffmpeg.channelLayouts() shouldBe channelLayouts

    verify(exactly = 1) { runFunc.run(listOf(FFmpeg.DEFAULT_PATH, "-layouts")) }
  }
}
