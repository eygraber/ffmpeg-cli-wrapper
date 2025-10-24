package net.bramp.ffmpeg

import net.bramp.ffmpeg.lang.MockProcess
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

/** Tests what happens when using avconv */
@RunWith(MockitoJUnitRunner::class)
class FFmpegAvTest {

  private val runFunc = ProcessFunction { args ->
    MockProcess(Helper.loadResource("avconv-version"))
  }

  private lateinit var ffmpeg: FFmpeg

  @Before
  fun before() {
    ffmpeg = FFmpeg(FFmpeg.DEFAULT_PATH, runFunc)
  }

  @Test
  @Throws(Exception::class)
  fun testVersion() {
    assertEquals(
      "avconv version 11.4, Copyright (c) 2000-2014 the Libav developers",
      ffmpeg.version(),
    )
    assertEquals(
      "avconv version 11.4, Copyright (c) 2000-2014 the Libav developers",
      ffmpeg.version(),
    )
  }

  /** We don't support avconv, so all methods should throw an exception. */
  @Test(expected = IllegalArgumentException::class)
  @Throws(IOException::class)
  fun testProbeVideo() {
    ffmpeg.run(emptyList())
  }

  @Test(expected = IllegalArgumentException::class)
  @Throws(IOException::class)
  fun testCodecs() {
    ffmpeg.codecs()
  }

  @Test(expected = IllegalArgumentException::class)
  @Throws(IOException::class)
  fun testFormats() {
    ffmpeg.formats()
  }
}
