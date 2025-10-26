package net.bramp.ffmpeg.kotlin.progress

import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.kotlin.Helper
import net.bramp.ffmpeg.kotlin.fixtures.allFiles
import net.bramp.ffmpeg.kotlin.fixtures.allProgresses
import net.bramp.ffmpeg.kotlin.fixtures.naProgressFile
import net.bramp.ffmpeg.kotlin.fixtures.naProgresses
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
        val inputStream = Helper.loadResource(progressFixture)
        val bytes = inputStream.readBytes()

        val packet = DatagramPacket(bytes, bytes.size, addr, port)
        socket.send(packet)
      }
    }

    Thread.sleep(1000) // HACK: Wait a short while to avoid closing the receiving socket

    parser.stop()

    progresses shouldBe allProgresses
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
        val inputStream = Helper.loadResource(progressFixture)
        val bytes = inputStream.readBytes()

        val packet = DatagramPacket(bytes, bytes.size, addr, port)
        socket.send(packet)
      }
    }

    Thread.sleep(100) // HACK: Wait a short while to avoid closing the receiving socket

    parser.stop()

    progresses shouldBe naProgresses
  }
}
