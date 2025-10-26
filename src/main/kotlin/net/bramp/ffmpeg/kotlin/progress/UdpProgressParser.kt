package net.bramp.ffmpeg.kotlin.progress

import java.io.IOException
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.URI
import java.util.concurrent.CountDownLatch

class UdpProgressParser @JvmOverloads constructor(
  listener: ProgressListener,
  port: Int = 0,
  addr: InetAddress? = InetAddress.getLoopbackAddress(),
) : AbstractSocketProgressParser(listener) {
  private val socket: DatagramSocket = DatagramSocket(port, addr)

  override val uri: URI = createUri("udp", socket.localAddress, socket.localPort)

  override val threadName = "UdpProgressParser"

  init {
    socket.broadcast = false
    // this.socket.setSoTimeout(); // TODO Setup timeouts
  }

  @Synchronized
  @Throws(IOException::class)
  override fun stop() {
    if(socket.isClosed) {
      // Allow double stop, and ignore
      return
    }
    socket.close()
    super.stop()
  }

  override fun getRunnable(startSignal: CountDownLatch): Runnable = UdpProgressParserRunnable(
    parser,
    socket,
    startSignal,
  )
}
