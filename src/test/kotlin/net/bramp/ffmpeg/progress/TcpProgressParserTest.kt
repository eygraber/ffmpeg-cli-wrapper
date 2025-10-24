package net.bramp.ffmpeg.progress

import com.google.common.io.ByteStreams
import net.bramp.ffmpeg.Helper.combineResource
import net.bramp.ffmpeg.fixtures.Progresses
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertTrue
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
    assertTrue("Socket is connected", client.isConnected)

    val inputStream = combineResource(Progresses.allFiles)
    val outputStream = client.getOutputStream()

    val bytes = ByteStreams.copy(inputStream, outputStream)

    // HACK, but give the TcpProgressParser thread time to actually handle the connection/data
    // before the client is closed, and the parser is stopped.
    Thread.sleep(100)

    client.close()
    parser.stop()

    assertThat(bytes, greaterThan(0L))
    assertThat(progresses, equalTo(Progresses.allProgresses))
  }

  @Test
  @Throws(IOException::class, InterruptedException::class, URISyntaxException::class)
  fun testNaProgressPackets() {
    parser.start()

    val client = Socket(uri.host, uri.port)
    assertTrue("Socket is connected", client.isConnected)

    val inputStream = combineResource(Progresses.naProgressFile)
    val outputStream = client.getOutputStream()

    val bytes = ByteStreams.copy(inputStream, outputStream)

    // HACK, but give the TcpProgressParser thread time to actually handle the connection/data
    // before the client is closed, and the parser is stopped.
    Thread.sleep(100)

    client.close()
    parser.stop()

    assertThat(bytes, greaterThan(0L))
    assertThat(progresses, equalTo(Progresses.naProgresses))
  }

  @Test
  @Throws(IOException::class)
  fun testPrematureDisconnect() {
    parser.start()
    Socket(uri.host, uri.port).close()
    parser.stop()

    assertTrue(progresses.isEmpty())
  }
}
