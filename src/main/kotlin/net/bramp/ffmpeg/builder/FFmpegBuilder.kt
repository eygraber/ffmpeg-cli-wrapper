package net.bramp.ffmpeg.builder

import com.google.common.base.Ascii
import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.Preconditions.checkNotEmpty
import net.bramp.ffmpeg.probe.FFmpegProbeResult
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.TreeMap
import java.util.concurrent.TimeUnit
import javax.annotation.CheckReturnValue

/**
 * Builds a ffmpeg command line
 *
 * @author bramp
 */
class FFmpegBuilder {
  /**
   * Log level options: [ffmpeg documentation](https://ffmpeg.org/ffmpeg.html#Generic-options)
   */
  enum class Verbosity {
    QUIET,
    PANIC,
    FATAL,
    ERROR,
    WARNING,
    INFO,
    VERBOSE,
    DEBUG,
    ;

    override fun toString(): String {
      // ffmpeg command line requires these options in lower case
      return Ascii.toLowerCase(name)
    }
  }

  // Global Settings
  private var override = true
  private var pass = 0
  private var passDirectory = ""
  private var passPrefix: String? = null
  private var verbosity = Verbosity.ERROR
  private var progress: URI? = null
  private var userAgent: String? = null
  private var qscale: Int? = null
  private var threads = 0

  // Input settings
  private var format: String? = null
  private var startOffset: Long? = null // in millis
  private var readAtNativeFrameRate = false
  internal val inputs: MutableList<AbstractFFmpegInputBuilder<*>> = ArrayList()
  val inputProbes: MutableMap<String, FFmpegProbeResult> = TreeMap()
  private val extraArgs: MutableList<String> = ArrayList()

  // Output
  private val outputs: MutableList<AbstractFFmpegOutputBuilder<*>> = ArrayList()
  private var strict = Strict.NORMAL

  // Filters
  private var audioFilter: String? = null
  private var videoFilter: String? = null
  private var complexFilter: String? = null

  fun setStrict(strict: Strict): FFmpegBuilder {
    this.strict = strict
    return this
  }

  fun overrideOutputFiles(override: Boolean): FFmpegBuilder {
    this.override = override
    return this
  }

  val overrideOutputFiles: Boolean
    get() = override

  fun setPass(pass: Int): FFmpegBuilder {
    this.pass = pass
    return this
  }

  fun setPassDirectory(directory: String): FFmpegBuilder {
    passDirectory = directory
    return this
  }

  fun setPassPrefix(prefix: String): FFmpegBuilder {
    passPrefix = prefix
    return this
  }

  fun setVerbosity(verbosity: Verbosity): FFmpegBuilder {
    this.verbosity = verbosity
    return this
  }

  fun setUserAgent(userAgent: String): FFmpegBuilder {
    this.userAgent = userAgent
    return this
  }

  /**
   * Makes ffmpeg read the first input at the native frame read
   *
   * @return this
   * @deprecated Use [AbstractFFmpegInputBuilder.readAtNativeFrameRate] instead
   */
  @Deprecated("")
  fun readAtNativeFrameRate(): FFmpegBuilder {
    readAtNativeFrameRate = true
    return this
  }

  fun addInput(result: FFmpegProbeResult): FFmpegFileInputBuilder {
    val filename = result.format!!.filename
    return doAddInput(FFmpegFileInputBuilder(this, filename, result))
  }

  fun addInput(filename: String): FFmpegFileInputBuilder = doAddInput(FFmpegFileInputBuilder(this, filename))

  fun <T : AbstractFFmpegInputBuilder<T>?> addInput(input: T): FFmpegBuilder = doAddInput(input)!!.done()

  private fun <T : AbstractFFmpegInputBuilder<T>?> doAddInput(input: T): T {
    inputs.add(input!!)
    return input
  }

  private fun clearInputs() {
    inputs.clear()
    inputProbes.clear()
  }

  fun setInput(result: FFmpegProbeResult): FFmpegFileInputBuilder {
    clearInputs()
    return addInput(result)
  }

  fun setInput(filename: String): FFmpegFileInputBuilder {
    clearInputs()
    return addInput(filename)
  }

  fun <T : AbstractFFmpegInputBuilder<T>?> setInput(input: T): FFmpegBuilder {
    clearInputs()
    inputs.add(input!!)
    return this
  }

  fun setThreads(threads: Int): FFmpegBuilder {
    Preconditions.checkArgument(threads > 0, "threads must be greater than zero")
    this.threads = threads
    return this
  }

  /**
   * Sets the format for the first input stream
   *
   * @param format, the format of this input stream, not null
   * @return this
   * @deprecated Specify this option on an input stream using [ ][AbstractFFmpegStreamBuilder.setFormat]
   */
  @Deprecated("")
  fun setFormat(format: String): FFmpegBuilder {
    this.format = format
    return this
  }

  /**
   * Sets the start offset for the first input stream
   *
   * @param duration the amount of the offset, measured in terms of the unit
   * @param units the unit that the duration is measured in, not null
   * @return this
   * @deprecated Specify this option on an input or output stream using [ ][AbstractFFmpegStreamBuilder.setStartOffset]
   */
  @Deprecated("")
  fun setStartOffset(duration: Long, units: TimeUnit): FFmpegBuilder {
    startOffset = units.toMillis(duration)
    return this
  }

  fun addProgress(uri: URI): FFmpegBuilder {
    progress = uri
    return this
  }

  /**
   * Sets the complex filter flag.
   *
   * @param filter the complex filter string
   * @return this
   * @deprecated Use [AbstractFFmpegOutputBuilder.setComplexFilter] instead
   */
  @Deprecated("")
  fun setComplexFilter(filter: String?): FFmpegBuilder {
    complexFilter = checkNotEmpty(filter, "filter must not be empty")
    return this
  }

  /**
   * Sets the audio filter flag.
   *
   * @param filter the audio filter string
   * @return this
   */
  fun setAudioFilter(filter: String?): FFmpegBuilder {
    audioFilter = checkNotEmpty(filter, "filter must not be empty")
    return this
  }

  /**
   * Sets the video filter flag.
   *
   * @param filter the video filter string
   * @return this
   */
  fun setVideoFilter(filter: String?): FFmpegBuilder {
    videoFilter = checkNotEmpty(filter, "filter must not be empty")
    return this
  }

  /**
   * Sets vbr quality when decoding mp3 output.
   *
   * @param quality the quality between 0 and 9. Where 0 is best.
   * @return FFmpegBuilder
   */
  fun setVBR(quality: Int?): FFmpegBuilder {
    Preconditions.checkArgument(quality!! > 0 && quality < 9, "vbr must be between 0 and 9")
    qscale = quality
    return this
  }

  /**
   * Add additional ouput arguments (for flags which aren't currently supported).
   *
   * @param values The extra arguments.
   * @return this
   */
  fun addExtraArgs(vararg values: String): FFmpegBuilder {
    Preconditions.checkArgument(values.isNotEmpty(), "one or more values must be supplied")
    checkNotEmpty(values[0], "first extra arg may not be empty")
    for(value in values) {
      extraArgs.add(value)
    }
    return this
  }

  /**
   * Adds new output file.
   *
   * @param filename output file path
   * @return A new [FFmpegOutputBuilder]
   */
  fun addOutput(filename: String): FFmpegOutputBuilder {
    val output = FFmpegOutputBuilder(this, filename)
    outputs.add(output)
    return output
  }

  /**
   * Adds new output file.
   *
   * @param uri output file uri typically a stream
   * @return A new [FFmpegOutputBuilder]
   */
  fun addOutput(uri: URI): FFmpegOutputBuilder {
    val output = FFmpegOutputBuilder(this, uri)
    outputs.add(output)
    return output
  }

  /**
   * Adds new HLS(Http Live Streaming) output file. <br></br>
   *
   * <pre>
   * `List<String> args = new FFmpegBuilder()
   * .addHlsOutput("output.m3u8")
   * .done().build();`
   </String> * </pre>
   *
   * @param filename output file path
   * @return A new [FFmpegHlsOutputBuilder]
   */
  fun addHlsOutput(filename: String): FFmpegHlsOutputBuilder {
    val output = FFmpegHlsOutputBuilder(this, filename)
    outputs.add(output)
    return output
  }

  /**
   * Adds an existing FFmpegOutputBuilder. This is similar to calling the other addOuput methods but
   * instead allows an existing FFmpegOutputBuilder to be used, and reused.
   *
   * <pre>
   * `List<String> args = new FFmpegBuilder()
   * .addOutput(new FFmpegOutputBuilder()
   * .setFilename("output.flv")
   * .setVideoCodec("flv")
   * )
   * .build();`
   </String> * </pre>
   *
   * @param output FFmpegOutputBuilder to add
   * @return this
   */
  fun addOutput(output: FFmpegOutputBuilder): FFmpegBuilder {
    outputs.add(output)
    return this
  }

  /**
   * Create new output (to stdout)
   *
   * @return A new [FFmpegOutputBuilder]
   */
  fun addStdoutOutput(): FFmpegOutputBuilder = addOutput("-")

  @CheckReturnValue
  fun build(): List<String> {
    val args = ImmutableList.builder<String>()
    Preconditions.checkArgument(inputs.isNotEmpty(), "At least one input must be specified")
    Preconditions.checkArgument(outputs.isNotEmpty(), "At least one output must be specified")
    if(strict != Strict.NORMAL) {
      args.add("-strict", strict.toString())
    }
    args.add(if(override) "-y" else "-n")
    args.add("-v", verbosity.toString())
    if(userAgent != null) {
      args.add("-user_agent", userAgent)
    }
    if(startOffset != null) {
      log.warn(
        "Using FFmpegBuilder#setStartOffset is deprecated. Specify it on the inputStream or outputStream instead",
      )
      args.add("-ss", FFmpegUtils.toTimecode(startOffset!!, TimeUnit.MILLISECONDS))
    }
    if(threads > 0) {
      args.add("-threads", threads.toString())
    }
    if(format != null) {
      log.warn(
        "Using FFmpegBuilder#setFormat is deprecated. Specify it on the inputStream or outputStream instead",
      )
      args.add("-f", format)
    }
    if(readAtNativeFrameRate) {
      log.warn(
        "Using FFmpegBuilder#readAtNativeFrameRate is deprecated. Specify it on the inputStream instead",
      )
      args.add("-re")
    }
    if(progress != null) {
      args.add("-progress", progress.toString())
    }
    args.addAll(extraArgs)
    for(input in inputs) {
      args.addAll(input.build(pass))
    }
    if(pass > 0) {
      args.add("-pass", pass.toString())
      if(passPrefix != null) {
        args.add("-passlogfile", passDirectory + passPrefix)
      }
    }
    if(!Strings.isNullOrEmpty(audioFilter)) {
      args.add("-af", audioFilter)
    }
    if(!Strings.isNullOrEmpty(videoFilter)) {
      args.add("-vf", videoFilter)
    }
    if(!Strings.isNullOrEmpty(complexFilter)) {
      log.warn(
        "Using FFmpegBuilder#setComplexFilter is deprecated. Specify it on the outputStream instead",
      )
      args.add("-filter_complex", complexFilter)
    }
    if(qscale != null) {
      args.add("-qscale:a", qscale.toString())
    }
    for(output in outputs) {
      args.addAll(output.build(this, pass))
    }
    return args.build()
  }

  companion object {
    private val log = LoggerFactory.getLogger(FFmpegBuilder::class.java)
  }
}
