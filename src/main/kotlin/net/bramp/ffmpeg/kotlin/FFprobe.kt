package net.bramp.ffmpeg.kotlin

import kotlinx.serialization.serializer
import net.bramp.ffmpeg.kotlin.builder.FFprobeBuilder
import net.bramp.ffmpeg.kotlin.io.LoggingFilterReader
import net.bramp.ffmpeg.kotlin.probe.FFmpegProbeResult
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.Reader

class FFprobe(
  private val ffprobePath: String,
  private val function: ProcessFunction = RunProcessFunction(),
) {

  constructor(ffprobePath: String) : this(ffprobePath, RunProcessFunction())

  /**
   * Returns the version of the ffprobe client.
   *
   * @return the version of the ffprobe client.
   * @throws IOException If the process fails to start.
   */
  @Throws(IOException::class)
  fun version(): String {
    val process = function.run(listOf(ffprobePath, "-version"))
    try {
      return process.inputStream.bufferedReader().use { it.readLine() ?: "" }
    }
    finally {
      // inputStream is closed by .use above
      process.outputStream?.close()
      process.errorStream?.close()
      process.destroy()
    }
  }

  /**
   * Run ffprobe against the media file.
   *
   * @param mediaPath The path to the media file.
   * @return The ffprobe results.
   * @throws IOException If the process fails to start.
   */
  @Throws(IOException::class)
  fun probe(mediaPath: String): FFmpegProbeResult {
    val args = listOf(
      ffprobePath,
      "-v",
      "quiet",
      "-print_format",
      "json",
      "-show_error",
      "-show_format",
      "-show_streams",
      "-show_chapters",
      mediaPath,
    )

    return probe(args)
  }

  /**
   * Run ffprobe against the media file with optional user agent.
   *
   * @param mediaPath The path to the media file.
   * @param userAgent Optional user agent string.
   * @return The ffprobe results.
   * @throws IOException If the process fails to start.
   */
  @Throws(IOException::class)
  fun probe(mediaPath: String, userAgent: String?): FFmpegProbeResult {
    val args = buildList {
      add(ffprobePath)
      add("-v")
      add("quiet")
      add("-print_format")
      add("json")
      add("-show_error")
      if(userAgent != null) {
        add("-user_agent")
        add(userAgent)
      }
      add("-show_format")
      add("-show_streams")
      add("-show_chapters")
      add(mediaPath)
    }

    return probe(args)
  }

  /**
   * Run ffprobe against the media file with optional user agent and extra arguments.
   *
   * @param mediaPath The path to the media file.
   * @param userAgent Optional user agent string.
   * @param extraArgs Additional arguments to pass to ffprobe.
   * @return The ffprobe results.
   * @throws IOException If the process fails to start.
   */
  @Throws(IOException::class)
  fun probe(mediaPath: String, userAgent: String?, vararg extraArgs: String): FFmpegProbeResult {
    val args = buildList {
      add(ffprobePath)
      add("-v")
      add("quiet")
      add("-print_format")
      add("json")
      add("-show_error")
      if(userAgent != null) {
        add("-user_agent")
        add(userAgent)
      }
      addAll(extraArgs)
      add("-show_format")
      add("-show_streams")
      add("-show_chapters")
      add(mediaPath)
    }

    return probe(args)
  }

  /**
   * Run ffprobe with a custom set of arguments.
   *
   * @param arguments The arguments to pass to ffprobe.
   * @return The ffprobe results.
   * @throws IOException If the process fails to start.
   */
  @Throws(IOException::class)
  @Suppress("ThrowsCount")
  fun probe(arguments: List<String>): FFmpegProbeResult {
    // Prepend ffprobePath if it's not already the first argument
    val fullArgs = if(arguments.isEmpty() || arguments[0] != ffprobePath) {
      listOf(ffprobePath) + arguments
    }
    else {
      arguments
    }

    val process = function.run(fullArgs)
    val reader: Reader = LoggingFilterReader(
      process.inputStream.bufferedReader(),
      LOG,
    )

    try {
      val result: FFmpegProbeResult? = FFmpegUtils.json.decodeFromString(
        serializer<FFmpegProbeResult>(),
        reader.readText(),
      )

      if (result == null) {
        throw IOException("FFprobe returned no output")
      }

      // Check if the result contains an error and throw FFmpegException
      if(result.hasError()) {
        throw FFmpegException("FFprobe command failed", result.error)
      }

      return result
    }
    catch(e: FFmpegException) {
      // Re-throw FFmpegException as-is
      throw e
    }
    catch(e: Exception) {
      throw IOException("Failed to parse ffprobe output", e)
    }
    finally {
      process.outputStream?.close()
      process.errorStream?.close()
      process.inputStream.close() // Reader doesn't close the underlying stream
      process.destroy()
    }
  }

  /**
   * Run ffprobe with a custom builder.
   *
   * @param builder The FFprobeBuilder.
   * @return The ffprobe results.
   * @throws IOException If the process fails to start.
   */
  @Throws(IOException::class)
  fun probe(builder: FFprobeBuilder): FFmpegProbeResult {
    val args = listOf(ffprobePath) + builder.build()
    return probe(args)
  }

  /**
   * Throw an error if the previous ffprobe command failed.
   *
   * @param exitCode The exit code of the process.
   * @throws IOException If the process failed.
   */
  @Throws(IOException::class)
  fun throwOnError(exitCode: Int) {
    if(exitCode != 0) {
      throw IOException("FFprobe failed with exit code $exitCode")
    }
  }

  /**
   * Throw an error if the process failed or if the result contains an error.
   *
   * @param process The process that was executed.
   * @param result The probe result (may be null).
   * @throws FFmpegException If the process failed or result contains error.
   */
  @Throws(FFmpegException::class)
  fun throwOnError(process: Process, result: FFmpegProbeResult?) {
    val exitCode = process.exitValue()
    if(exitCode != 0) {
      throw FFmpegException("FFprobe failed with exit code $exitCode", result?.error)
    }
    if(result?.error != null) {
      throw FFmpegException("FFprobe returned an error", result.error)
    }
  }

  override fun toString(): String = "FFprobe(ffprobe_path=$ffprobePath)"

  fun builder(): FFprobeBuilder = FFprobeBuilder()

  companion object {
    private val LOG = LoggerFactory.getLogger(FFprobe::class.java)
    const val FFPROBE_COMMAND: String = "ffprobe"

    @JvmField
    @Suppress("ObjectPropertyNaming")
    val DEFAULT_PATH: String = System.getenv("FFPROBE") ?: FFPROBE_COMMAND
  }
}
