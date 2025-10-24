package net.bramp.ffmpeg.progress

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.Helper.combineResource
import net.bramp.ffmpeg.fixtures.allFiles
import net.bramp.ffmpeg.fixtures.allProgresses
import net.bramp.ffmpeg.fixtures.naProgressFile
import net.bramp.ffmpeg.fixtures.naProgresses
import org.junit.Test
import java.io.IOException
import java.net.Socket
import java.net.URISyntaxException

class TcpProgressParserTest : AbstractProgressParserTest() {

  override fun newParser(listener: ProgressListener): ProgressParser = TcpProgressParser(listener)

  @Test
  @Throws(IOException::class, InterruptedException::class)
  fun testNormal() {
    parser.start()

    val client = Socket(uri.host, uri.port)
    client.isConnected shouldBe true

    val inputStream = combineResource(allFiles)
    val outputStream = client.getOutputStream()

    val bytes = inputStream.copyTo(outputStream)

    // HACK, but give the TcpProgressParser thread time to actually handle the connection/data
    // before the client is closed, and the parser is stopped.
    Thread.sleep(100)

    client.close()
    parser.stop()

    bytes shouldBeGreaterThan 0L
    progresses shouldBe allProgresses
  }

  @Test
  @Throws(IOException::class, InterruptedException::class, URISyntaxException::class)
  fun testNaProgressPackets() {
    parser.start()

    val client = Socket(uri.host, uri.port)
    client.isConnected shouldBe true

    val inputStream = combineResource(naProgressFile)
    val outputStream = client.getOutputStream()

    val bytes = inputStream.copyTo(outputStream)

    // HACK, but give the TcpProgressParser thread time to actually handle the connection/data
    // before the client is closed, and the parser is stopped.
    Thread.sleep(100)

    client.close()
    parser.stop()

    bytes shouldBeGreaterThan 0L
    progresses shouldBe naProgresses
  }

  @Test
  @Throws(IOException::class)
  fun testPrematureDisconnect() {
    parser.start()
    Socket(uri.host, uri.port).close()
    parser.stop()

    progresses.shouldBeEmpty()
  }
}
