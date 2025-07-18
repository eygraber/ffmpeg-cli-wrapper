package net.bramp.ffmpeg

import com.google.common.base.MoreObjects
import com.google.gson.Gson
import net.bramp.ffmpeg.builder.FFprobeBuilder
import net.bramp.ffmpeg.io.LoggingFilterReader
import net.bramp.ffmpeg.probe.FFmpegProbeResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.Reader

/**
 * Wrapper around FFprobe
 *
 * @author bramp
 */
class FFprobe : FFcommon {

  @Throws(IOException::class)
  constructor() : this(DEFAULT_PATH, RunProcessFunction())

  @Throws(IOException::class)
  constructor(runFunction: ProcessFunction) : this(DEFAULT_PATH, runFunction)

  @Throws(IOException::class)
  constructor(path: String) : this(path, RunProcessFunction())

  constructor(path: String, runFunction: ProcessFunction) : super(path, runFunction)

  /**
   * Returns true if the binary we are using is the true ffprobe. This is to avoid conflict with
   * avprobe (from the libav project), that some symlink to ffprobe.
   *
   * @return true iff this is the official ffprobe binary.
   * @throws IOException If a I/O error occurs while executing ffprobe.
   */
  @Throws(IOException::class)
  fun isFfprobe(): Boolean = version().startsWith("ffprobe")

  /**
   * Throws an exception if this is an unsupported version of ffprobe.
   *
   * @throws IllegalArgumentException if this is not the official ffprobe binary.
   * @throws IOException If a I/O error occurs while executing ffprobe.
   */
  @Throws(IllegalArgumentException::class, IOException::class)
  private fun checkIfFfprobe() {
    require(isFfprobe()) { "This binary '$path' is not a supported version of ffprobe" }
  }

  @Throws(IOException::class)
  @JvmOverloads
  fun probe(
    mediaPath: String,
    userAgent: String? = null,
  ): FFmpegProbeResult = probe(this.builder().setInput(mediaPath).setUserAgent(userAgent))

  @Throws(IOException::class)
  fun probe(builder: FFprobeBuilder): FFmpegProbeResult =
    probe(builder.build())

  @Throws(IOException::class)
  fun probe(mediaPath: String, userAgent: String?, vararg extraArgs: String): FFmpegProbeResult = probe(
    builder()
      .setInput(mediaPath)
      .setUserAgent(userAgent)
      .addExtraArgs(*extraArgs)
      .build(),
  )

  @Throws(IOException::class)
  fun probe(args: List<String>): FFmpegProbeResult {
    checkIfFfprobe()

    val p = runFunc.run(path(args))
    try {
      var reader: Reader = wrapInReader(p)
      if (logger.isDebugEnabled) {
        reader = LoggingFilterReader(reader, logger)
      }

      val result: FFmpegProbeResult? = gson.fromJson(reader, FFmpegProbeResult::class.java)

      // throwOnError is called after parsing, so if parsing fails (e.g. empty output),
      // an exception might be thrown here first by gson.
      // If parsing succeeds but process had an error, throwOnError will catch it.
      throwOnError(p, result)

      if (result == null) {
        // This can happen if ffprobe output is empty or not valid JSON,
        // and throwOnError didn't catch an error (e.g. exit code 0 for some reason).
        throw IOException("FFprobe returned empty or invalid output, resulting in null probe result for args: $args")
      }

      return result
    }
    finally {
      p.destroy()
    }
  }

  fun builder(): FFprobeBuilder = FFprobeBuilder()

  companion object {
    internal val logger: Logger = LoggerFactory.getLogger(FFprobe::class.java)

    internal const val FFPROBE_COMMAND = "ffprobe"

    @Suppress("ObjectPropertyNaming")
    @JvmField // For Java compatibility as public static final
    val DEFAULT_PATH: String = MoreObjects.firstNonNull(System.getenv("FFPROBE"), FFPROBE_COMMAND)

    // Assumes FFmpegUtils.kt is already converted and its gson is accessible (e.g. internal)
    internal val gson: Gson by lazy { FFmpegUtils.gson }
  }
}
