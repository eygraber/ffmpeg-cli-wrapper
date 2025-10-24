package net.bramp.ffmpeg

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.lang.MockProcess
import org.junit.Before
import org.junit.Test
import java.io.IOException

/** Tests what happens when using avconv */
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
    ffmpeg.version() shouldBe "avconv version 11.4, Copyright (c) 2000-2014 the Libav developers"
    ffmpeg.version() shouldBe "avconv version 11.4, Copyright (c) 2000-2014 the Libav developers"
  }

  /** We don't support avconv, so all methods should throw an exception. */
  @Test
  @Throws(IOException::class)
  fun testProbeVideo() {
    shouldThrow<IllegalArgumentException> {
      ffmpeg.run(emptyList())
    }
  }

  @Test
  @Throws(IOException::class)
  fun testCodecs() {
    shouldThrow<IllegalArgumentException> {
      ffmpeg.codecs()
    }
  }

  @Test
  @Throws(IOException::class)
  fun testFormats() {
    shouldThrow<IllegalArgumentException> {
      ffmpeg.formats()
    }
  }
}
