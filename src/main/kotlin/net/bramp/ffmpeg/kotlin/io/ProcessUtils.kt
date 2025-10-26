package net.bramp.ffmpeg.kotlin.io

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * A collection of utility methods for dealing with processes.
 *
 * @author bramp
 */
object ProcessUtils {
  /**
   * Waits until a process finishes or a timeout occurs
   *
   * @param p process
   * @param timeout timeout in given unit
   * @param unit time unit
   * @return the process exit value
   * @throws TimeoutException if a timeout occurs
   */
  @Throws(TimeoutException::class)
  fun waitForWithTimeout(p: Process, timeout: Long, unit: TimeUnit): Int {
    try {
      p.waitFor(timeout, unit)
    }
    catch(e: InterruptedException) {
      Thread.currentThread().interrupt()
    }
    if(p.isAlive) {
      throw TimeoutException("Process did not finish within timeout")
    }
    return p.exitValue()
  }
}
