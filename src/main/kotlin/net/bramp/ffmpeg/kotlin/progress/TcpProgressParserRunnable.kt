package net.bramp.ffmpeg.kotlin.progress

import java.io.IOException
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.CountDownLatch

internal class TcpProgressParserRunnable(
  val parser: StreamProgressParser,
  val server: ServerSocket,
  val startSignal: CountDownLatch,
) : Runnable {
  override fun run() {
    while(!server.isClosed && !Thread.currentThread().isInterrupted) {
      try {
        // There is a subtle race condition, where ffmpeg can start up, and close before this thread
        // is scheduled. This happens more often on Travis than a unloaded system.
        startSignal.countDown()
        server.accept().use { socket ->
          parser.processStream(socket.getInputStream())
        }
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
}
