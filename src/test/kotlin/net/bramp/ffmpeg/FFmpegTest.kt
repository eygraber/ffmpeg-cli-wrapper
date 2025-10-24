package net.bramp.ffmpeg

import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.fixtures.*
import net.bramp.ffmpeg.lang.NewProcessAnswer
import org.hamcrest.Matchers.hasItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.hamcrest.MockitoHamcrest.argThat
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit
import org.junit.Assert.*

@RunWith(MockitoJUnitRunner::class)
class FFmpegTest {
  @Mock
  private lateinit var runFunc: ProcessFunction

  private lateinit var ffmpeg: FFmpeg

  @Before
  fun before() {
    `when`(runFunc.run(argThatHasItem("-version")))
      .thenAnswer(NewProcessAnswer("ffmpeg-version"))
    `when`(runFunc.run(argThatHasItem("-formats")))
      .thenAnswer(NewProcessAnswer("ffmpeg-formats"))
    `when`(runFunc.run(argThatHasItem("-codecs")))
      .thenAnswer(NewProcessAnswer("ffmpeg-codecs"))
    `when`(runFunc.run(argThatHasItem("-pix_fmts")))
      .thenAnswer(NewProcessAnswer("ffmpeg-pix_fmts"))
    `when`(runFunc.run(argThatHasItem("toto.mp4")))
      .thenAnswer(NewProcessAnswer("ffmpeg-version", "ffmpeg-no-such-file"))
    `when`(runFunc.run(argThatHasItem("-filters")))
      .thenAnswer(NewProcessAnswer("ffmpeg-filters"))
    `when`(runFunc.run(argThatHasItem("-layouts")))
      .thenAnswer(NewProcessAnswer("ffmpeg-layouts"))

    ffmpeg = FFmpeg(FFmpeg.DEFAULT_PATH, runFunc)
  }

  @Test
  fun testVersion() {
    assertEquals("ffmpeg version 0.10.9-7:0.10.9-1~raring1", ffmpeg.version())
    assertEquals("ffmpeg version 0.10.9-7:0.10.9-1~raring1", ffmpeg.version())

    verify(runFunc, times(1)).run(argThatHasItem("-version"))
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
    assertEquals(Codecs.CODECS, ffmpeg.codecs())
    assertEquals(Codecs.CODECS, ffmpeg.codecs())

    verify(runFunc, times(1)).run(argThatHasItem("-codecs"))
  }

  @Test
  fun testFormats() {
    // Run twice, the second should be cached
    assertEquals(Formats.FORMATS, ffmpeg.formats())
    assertEquals(Formats.FORMATS, ffmpeg.formats())

    verify(runFunc, times(1)).run(argThatHasItem("-formats"))
  }

  @Test
  fun testReadProcessStreams() {
    // process input stream
    val processInputStream = mock(Appendable::class.java)
    ffmpeg.processOutputStream = processInputStream
    // process error stream
    val processErrStream = mock(Appendable::class.java)
    ffmpeg.processErrorStream = processErrStream
    // run ffmpeg with non existing file
    ffmpeg.run(listOf("-i", "toto.mp4"))
    // check calls to Appendables
    verify(processInputStream, times(1)).append(any(CharSequence::class.java))
    verify(processErrStream, times(1)).append(any(CharSequence::class.java))
  }

  @Test
  fun testPixelFormat() {
    // Run twice, the second should be cached
    assertEquals(PixelFormats.PIXEL_FORMATS, ffmpeg.pixelFormats())
    assertEquals(PixelFormats.PIXEL_FORMATS, ffmpeg.pixelFormats())

    verify(runFunc, times(1)).run(argThatHasItem("-pix_fmts"))
  }

  @Test
  fun testFilters() {
    // Run twice, the second should be cached
    val filters = ffmpeg.filters()!!

    for(i in filters.indices) {
      assertEquals(Filters.FILTERS!![i], filters[i])
    }

    assertEquals(Filters.FILTERS, ffmpeg.filters())
    assertEquals(Filters.FILTERS, ffmpeg.filters())

    verify(runFunc, times(1)).run(argThatHasItem("-filters"))
  }

  @Test
  fun testLayouts() {
    assertEquals(ChannelLayouts.CHANNEL_LAYOUTS, ffmpeg.channelLayouts())
    assertEquals(ChannelLayouts.CHANNEL_LAYOUTS, ffmpeg.channelLayouts())

    verify(runFunc, times(1)).run(argThatHasItem("-layouts"))
  }

  companion object {
    @Suppress("UNCHECKED_CAST")
    fun <T> argThatHasItem(item: T): List<T> =
      org.mockito.hamcrest.MockitoHamcrest.argThat(hasItem(item)) as List<T>
  }
}
