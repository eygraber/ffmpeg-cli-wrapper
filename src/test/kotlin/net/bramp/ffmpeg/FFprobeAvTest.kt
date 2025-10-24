package net.bramp.ffmpeg

import net.bramp.ffmpeg.FFmpegTest.Companion.argThatHasItem
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.lang.NewProcessAnswer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

/** Tests what happens when using avprobe */
@RunWith(MockitoJUnitRunner::class)
class FFprobeAvTest {

  @Mock
  private lateinit var runFunc: ProcessFunction

  private lateinit var ffprobe: FFprobe

  @Before
  @Throws(IOException::class)
  fun before() {
    `when`(runFunc.run(argThatHasItem("-version")))
      .thenAnswer(NewProcessAnswer("avprobe-version"))

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
