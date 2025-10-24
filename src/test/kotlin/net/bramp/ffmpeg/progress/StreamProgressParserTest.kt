package net.bramp.ffmpeg.progress

import net.bramp.ffmpeg.Helper.combineResource
import net.bramp.ffmpeg.fixtures.Progresses
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import java.io.IOException

class StreamProgressParserTest {
  private val listener = RecordingProgressListener()

  @Test
  @Throws(IOException::class)
  fun testNormal() {
    listener.reset()

    val parser = StreamProgressParser(listener)

    val inputStream = combineResource(Progresses.allFiles)
    parser.processStream(inputStream)

    assertThat(listener.progresses, equalTo(Progresses.allProgresses))
  }

  @Test
  @Throws(IOException::class)
  fun testNaProgressPackets() {
    listener.reset()

    val parser = StreamProgressParser(listener)

    val inputStream = combineResource(Progresses.naProgressFile)
    parser.processStream(inputStream)

    assertThat(listener.progresses, equalTo(Progresses.naProgresses))
  }
}
