package net.bramp.ffmpeg.kotlin.progress

import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.kotlin.Helper
import net.bramp.ffmpeg.kotlin.fixtures.allFiles
import net.bramp.ffmpeg.kotlin.fixtures.allProgresses
import net.bramp.ffmpeg.kotlin.fixtures.naProgressFile
import net.bramp.ffmpeg.kotlin.fixtures.naProgresses
import org.junit.Test
import java.io.IOException

class StreamProgressParserTest {
  private val listener = RecordingProgressListener()

  @Test
  @Throws(IOException::class)
  fun testNormal() {
    listener.reset()

    val parser = StreamProgressParser(listener)

    val inputStream = Helper.combineResource(allFiles)
    parser.processStream(inputStream)

    listener.progresses shouldBe allProgresses
  }

  @Test
  @Throws(IOException::class)
  fun testNaProgressPackets() {
    listener.reset()

    val parser = StreamProgressParser(listener)

    val inputStream = Helper.combineResource(naProgressFile)
    parser.processStream(inputStream)

    listener.progresses shouldBe naProgresses
  }
}
