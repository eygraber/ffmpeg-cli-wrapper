package net.bramp.ffmpeg.kotlin.progress

import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.util.concurrent.CountDownLatch

internal class UdpProgressParserRunnable(
  val parser: StreamProgressParser,
  val socket: DatagramSocket,
  val startSignal: CountDownLatch,
) : Runnable {
  override fun run() {
    val buf = ByteArray(MAX_PACKET_SIZE)
    val packet = DatagramPacket(buf, buf.size)
    while(!socket.isClosed && !Thread.currentThread().isInterrupted) {
      startSignal.countDown()
      try {
        // TODO This doesn't handle the case of a progress being split across two packets
        socket.receive(packet)
        if (packet.length == 0) {
          continue
        }
        parser.processStream(
          ByteArrayInputStream(packet.data, packet.offset, packet.length),
        )
      }
      catch (_: SocketException) {
        // Most likely a Socket closed exception, which we can safely ignore
      }
      catch (_: IOException) {
        // We have no good way to report this back to the user... yet
        // TODO Report to the user that this failed in some way
      }
    }
  }

  companion object {
    const val MAX_PACKET_SIZE = 1500
  }
}
