package net.bramp.ffmpeg.kotlin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import net.bramp.ffmpeg.kotlin.fixtures.Samples
import net.bramp.ffmpeg.kotlin.lang.MockProcess
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.IOException

/** Tests what happens when using avprobe */
class FFprobeAvTest {

  private lateinit var runFunc: ProcessFunction
  private lateinit var ffprobe: FFprobe

  @Before
  @Throws(IOException::class)
  fun before() {
    runFunc = mockk()
    every { runFunc.run(any()) } answers { MockProcess(Helper.loadResource("avprobe-version")) }

    ffprobe = FFprobe("ffprobe", runFunc)
  }

  @Test
  @Throws(Exception::class)
  fun testVersion() {
    ffprobe.version() shouldBe "avprobe version 11.4, Copyright (c) 2007-2014 the Libav developers"
    ffprobe.version() shouldBe "avprobe version 11.4, Copyright (c) 2007-2014 the Libav developers"
  }

  @Ignore("avprobe detection not implemented")
  @Test
  @Throws(IOException::class)
  fun testProbeVideo() {
    shouldThrow<IllegalArgumentException> {
      ffprobe.probe(Samples.big_buck_bunny_720p_1mb)
    }
  }
}
