package net.bramp.ffmpeg

import io.mockk.*
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.lang.MockProcess
import org.junit.Assert.assertEquals
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
    assertEquals(
      "avprobe version 11.4, Copyright (c) 2007-2014 the Libav developers",
      ffprobe.version()
    )
    assertEquals(
      "avprobe version 11.4, Copyright (c) 2007-2014 the Libav developers",
      ffprobe.version()
    )
  }

  @Ignore("avprobe detection not implemented")
  @Test(expected = IllegalArgumentException::class)
  @Throws(IOException::class)
  fun testProbeVideo() {
    ffprobe.probe(Samples.big_buck_bunny_720p_1mb)
  }
}
