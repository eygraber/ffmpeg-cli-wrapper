package net.bramp.ffmpeg.kotlin.progress

import java.io.IOException
import java.net.Inet6Address
import java.net.InetAddress
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.CountDownLatch

abstract class AbstractSocketProgressParser(listener: ProgressListener) : ProgressParser {
  val parser: StreamProgressParser = StreamProgressParser(listener)

  private var thread: Thread? = null // Thread for handling incoming connections

  protected abstract val threadName: String

  protected abstract fun getRunnable(startSignal: CountDownLatch): Runnable

  /**
   * Starts the ProgressParser waiting for progress.
   *
   * @throws IllegalThreadStateException if the parser was already started.
   */
  @Synchronized
  @Throws(IOException::class)
  override fun start() {
    if(thread != null) {
      throw IllegalThreadStateException("Parser already started")
    }
    val name = "$threadName($uri)"
    val startSignal = CountDownLatch(1)
    val runnable = getRunnable(startSignal)
    thread = Thread(runnable, name)
    thread?.start()

    // Block until the thread has started
    try {
      startSignal.await()
    }
    catch(e: InterruptedException) {
      Thread.currentThread().interrupt()
    }
  }

  @Throws(IOException::class)
  override fun stop() {
    thread?.interrupt() // This unblocks processStream();
    try {
      thread?.join()
    }
    catch(_: InterruptedException) {
      Thread.currentThread().interrupt()
    }
  }

  @Throws(IOException::class)
  override fun close() {
    stop()
  }

  companion object {
    /**
     * Creates a URL to parse to FFmpeg based on the scheme, address and port.
     *
     * TODO Move this method to somewhere better.
     *
     * @param scheme The scheme to use (e.g. "tcp", "udp", "rtp", "http")
     * @param address The address of the server
     * @param port The port to connect to
     * @return A URI representing the address and port
     * @throws URISyntaxException if the URI is invalid
     */
    @JvmStatic
    @Throws(URISyntaxException::class)
    fun createUri(scheme: String?, address: InetAddress, port: Int): URI {
      // Format IPv6 addresses with brackets, IPv4 without
      val hostString = if(address is Inet6Address) {
        "[${address.hostAddress}]"
      }
      else {
        address.hostAddress ?: address.toString()
      }

      return URI(
        scheme,
        null, /* userInfo */
        hostString,
        port,
        null, /* path */
        null, /* query */
        null, /* fragment */
      )
    }
  }
}
