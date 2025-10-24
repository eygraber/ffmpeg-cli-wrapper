package net.bramp.ffmpeg.progress

import com.google.common.io.ByteStreams
import net.bramp.ffmpeg.Helper.loadResource
import net.bramp.ffmpeg.fixtures.allFiles
import net.bramp.ffmpeg.fixtures.allProgresses
import net.bramp.ffmpeg.fixtures.naProgressFile
import net.bramp.ffmpeg.fixtures.naProgresses
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.URISyntaxException

class UdpProgressParserTest : AbstractProgressParserTest() {

  @Throws(IOException::class, URISyntaxException::class)
  override fun newParser(listener: ProgressListener): ProgressParser = UdpProgressParser(listener)

  @Test
  @Throws(IOException::class, InterruptedException::class)
  fun testNormal() {
    parser.start()

    val addr = InetAddress.getByName(uri.host)
    val port = uri.port

    DatagramSocket().use { socket ->
      // Load each Progress Fixture, and send in a single datagram packet
      for(progressFixture in allFiles) {
        val inputStream = loadResource(progressFixture)
        val bytes = ByteStreams.toByteArray(inputStream)

        val packet = DatagramPacket(bytes, bytes.size, addr, port)
        socket.send(packet)
      }
    }

    Thread.sleep(1000) // HACK: Wait a short while to avoid closing the receiving socket

    parser.stop()

    assertThat(progresses, equalTo(allProgresses))
  }

  @Test
  @Throws(IOException::class, InterruptedException::class, URISyntaxException::class)
  fun testNaProgressPackets() {
    parser.start()

    val addr = InetAddress.getByName(uri.host)
    val port = uri.port

    DatagramSocket().use { socket ->
      // Load each Progress Fixture, and send in a single datagram packet
      for(progressFixture in naProgressFile) {
        val inputStream = loadResource(progressFixture)
        val bytes = ByteStreams.toByteArray(inputStream)

        val packet = DatagramPacket(bytes, bytes.size, addr, port)
        socket.send(packet)
      }
    }

    Thread.sleep(100) // HACK: Wait a short while to avoid closing the receiving socket

    parser.stop()

    assertThat(progresses, equalTo(naProgresses))
  }
}
