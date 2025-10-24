package net.bramp.ffmpeg.progress

import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.Helper.combineResource
import net.bramp.ffmpeg.fixtures.allFiles
import net.bramp.ffmpeg.fixtures.allProgresses
import net.bramp.ffmpeg.fixtures.naProgressFile
import net.bramp.ffmpeg.fixtures.naProgresses
import org.junit.Test
import java.io.IOException

class StreamProgressParserTest {
  private val listener = RecordingProgressListener()

  @Test
  @Throws(IOException::class)
  fun testNormal() {
    listener.reset()

    val parser = StreamProgressParser(listener)

    val inputStream = combineResource(allFiles)
    parser.processStream(inputStream)

    listener.progresses shouldBe allProgresses
  }

  @Test
  @Throws(IOException::class)
  fun testNaProgressPackets() {
    listener.reset()

    val parser = StreamProgressParser(listener)

    val inputStream = combineResource(naProgressFile)
    parser.processStream(inputStream)

    listener.progresses shouldBe naProgresses
  }
}
