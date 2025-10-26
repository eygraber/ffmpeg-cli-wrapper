package net.bramp.ffmpeg.kotlin.progress

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.URI
import java.util.concurrent.CountDownLatch

class TcpProgressParser @JvmOverloads constructor(
  listener: ProgressListener,
  port: Int = 0,
  addr: InetAddress? = InetAddress.getLoopbackAddress(),
) : AbstractSocketProgressParser(listener) {
  private val server: ServerSocket = ServerSocket(port, 0, addr)
  override val uri: URI = createUri("tcp", server.inetAddress, server.localPort)

  override val threadName = "TcpProgressParser"

  @Synchronized
  @Throws(IOException::class)
  override fun stop() {
    if(server.isClosed) {
      // Allow double stop, and ignore
      return
    }
    server.close() // This unblocks server.accept();
    super.stop()
  }

  override fun getRunnable(startSignal: CountDownLatch): Runnable = TcpProgressParserRunnable(
    parser,
    server,
    startSignal,
  )
}
