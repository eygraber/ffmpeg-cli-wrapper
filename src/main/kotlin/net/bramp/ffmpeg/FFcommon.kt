package net.bramp.ffmpeg

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import com.google.common.io.CharStreams
import net.bramp.ffmpeg.io.ProcessUtils
import net.bramp.ffmpeg.probe.FFmpegError
import net.bramp.ffmpeg.probe.FFmpegProbeResult
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/** Private class to contain common methods for both FFmpeg and FFprobe. */
abstract class FFcommon protected constructor(
  val path: String,
  protected val runFunc: ProcessFunction = RunProcessFunction(),
) {

  init {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "path must not be null or empty")
  }

  /** Version string */
  var version: String? = null
    private set

  /** Process input stream */
  var processOutputStream: Appendable = System.out

  /** Process error stream */
  var processErrorStream: Appendable = System.err

  protected fun wrapInReader(inputStream: InputStream): BufferedReader =
    BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))

  protected fun wrapInReader(p: Process): BufferedReader = wrapInReader(p.inputStream)

  protected fun wrapErrorInReader(p: Process): BufferedReader = wrapInReader(p.errorStream)

  @Throws(IOException::class)
  protected fun throwOnError(p: Process) {
    try {
      if (ProcessUtils.waitForWithTimeout(p, 1, TimeUnit.SECONDS) != 0) {
        // TODO Parse the error
        throw IOException("$path returned non-zero exit status. Check stdout.")
      }
    }
    catch(e: TimeoutException) {
      throw IOException("Timed out waiting for $path to finish.", e)
    }
  }

  @Throws(IOException::class)
  @Suppress("CanBeNonNullable")
  protected fun throwOnError(p: Process, result: FFmpegProbeResult?) {
    try {
      if (ProcessUtils.waitForWithTimeout(p, 1, TimeUnit.SECONDS) != 0) {
        val ffmpegError: FFmpegError? = result?.error
        throw FFmpegException("$path returned non-zero exit status. Check stdout.", ffmpegError)
      }
    }
    catch(e: TimeoutException) {
      throw IOException("Timed out waiting for $path to finish.", e)
    }
  }

  /**
   * Returns the version string for this binary.
   *
   * @return the version string.
   * @throws IOException If there is an error capturing output from the binary or if the version cannot be read.
   */
  @Synchronized
  @Throws(IOException::class)
  fun version(): String {
    if(this.version == null) {
      val p = runFunc.run(ImmutableList.of(path, "-version"))
      try {
        val r = wrapInReader(p)
        val versionLine = r.readLine()
        CharStreams.copy(r, CharStreams.nullWriter()) // Throw away rest of the output
        throwOnError(p) // Check for process error first

        // If process didn't error, but versionLine is null, it's an unexpected output
        if (versionLine == null) {
          throw IOException(
            "Failed to read version from $path output: version line was null after successful process execution.",
          )
        }
        this.version = versionLine
      } finally {
        p.destroy()
      }
    }

    return requireNotNull(this.version) {
      "The version property should be guaranteed to be non-null if this point is reached without exception."
    }
  }

  /**
   * Returns the full path to the binary with arguments appended.
   *
   * @param args The arguments to pass to the binary.
   * @return The full path and arguments to execute the binary.
   */
  fun path(args: List<String>): List<String> = ImmutableList.builder<String>().add(path).addAll(args).build()

  /**
   * Runs the binary (ffmpeg) with the supplied args. Blocking until finished.
   *
   * @param args The arguments to pass to the binary.
   * @throws IOException If there is a problem executing the binary.
   */
  @Throws(IOException::class)
  open fun run(args: List<String>) {
    val p = runFunc.run(path(args))
    try {
      // TODO Move the copy onto a thread, so that FFmpegProgressListener can be on this thread.
      CharStreams.copy(wrapInReader(p), processOutputStream)
      CharStreams.copy(wrapErrorInReader(p), processErrorStream)
      throwOnError(p)
    }
    finally {
      p.destroy()
    }
  }
}
