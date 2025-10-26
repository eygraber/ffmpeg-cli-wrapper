package net.bramp.ffmpeg.kotlin.progress

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections
import java.util.concurrent.TimeUnit

abstract class AbstractProgressParserTest {
  @get:Rule
  val timeout = Timeout(10, TimeUnit.SECONDS)

  protected val progresses: MutableList<Progress> = Collections.synchronizedList(ArrayList())

  protected lateinit var parser: ProgressParser
  protected lateinit var uri: URI

  protected val listener = ProgressListener { progress -> progresses.add(progress) }

  @Before
  @Throws(IOException::class, URISyntaxException::class)
  fun setupParser() {
    synchronized(progresses) {
      progresses.clear()
    }

    parser = newParser(listener)
    uri = parser.uri
  }

  @Throws(IOException::class, URISyntaxException::class)
  abstract fun newParser(listener: ProgressListener): ProgressParser

  @Test
  @Throws(IOException::class)
  fun testNoConnection() {
    parser.start()
    parser.stop()
    progresses.shouldBeEmpty()
  }

  @Test
  @Throws(IOException::class)
  fun testDoubleStop() {
    parser.start()
    parser.stop()
    parser.stop()
    progresses.shouldBeEmpty()
  }

  @Test
  @Throws(IOException::class)
  fun testDoubleStart() {
    shouldThrow<IllegalThreadStateException> {
      parser.start()
      parser.start()
    }
    progresses.shouldBeEmpty()
  }

  @Test
  @Throws(IOException::class)
  fun testStopNoStart() {
    parser.stop()
    progresses.shouldBeEmpty()
  }
}
