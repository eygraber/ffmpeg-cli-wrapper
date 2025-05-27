package net.bramp.ffmpeg.builder

import com.google.common.base.Ascii
import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import java.net.URI
import java.util.ArrayList
import java.util.TreeMap // Not used in current snippet but was in original class structure
import java.util.concurrent.TimeUnit
import net.bramp.ffmpeg.FFmpegUtils 
import net.bramp.ffmpeg.Preconditions as FfmpegPreconditions // Alias for project's Preconditions
import net.bramp.ffmpeg.probe.FFmpegProbeResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FFmpegBuilder {

  enum class Verbosity {
    QUIET,
    PANIC,
    FATAL,
    ERROR,
    WARNING,
    INFO,
    VERBOSE,
    DEBUG;

    override fun toString(): String {
      return name.lowercase() 
    }
  }

  var override: Boolean = true
  var pass: Int = 0
  var pass_directory: String = ""
  var pass_prefix: String? = null
  var verbosity: Verbosity = Verbosity.ERROR
  var progress: URI? = null
  var user_agent: String? = null
  var qscale: Int? = null 

  var threads: Int = 0
  
  @Deprecated("Specify this option on an input stream using AbstractFFmpegStreamBuilder.setFormat(String)")
  var format: String? = null
  
  @Deprecated("Specify this option on an input or output stream using AbstractFFmpegStreamBuilder.setStartOffset(long, TimeUnit)")
  var startOffset: Long? = null
  
  @Deprecated("Use AbstractFFmpegInputBuilder.readAtNativeFrameRate() instead")
  var read_at_native_frame_rate: Boolean = false
  
  val inputs: MutableList<AbstractFFmpegInputBuilder<*>> = ArrayList()
  // val inputProbes: MutableMap<String, FFmpegProbeResult> = TreeMap() // If needed

  val extra_args: MutableList<String> = ArrayList()
  val outputs: MutableList<AbstractFFmpegOutputBuilder<*>> = ArrayList()
  var strict: Strict = Strict.NORMAL

  var audioFilter: String? = null
  var videoFilter: String? = null
  
  @Deprecated("Use AbstractFFmpegOutputBuilder.setComplexFilter(String) instead")
  var complexFilter: String? = null

  fun setStrict(strict: Strict): FFmpegBuilder {
    this.strict = Preconditions.checkNotNull(strict)
    return this
  }

  fun overrideOutputFiles(override: Boolean): FFmpegBuilder {
    this.override = override
    return this
  }

  fun getOverrideOutputFiles(): Boolean = this.override

  fun setPass(pass: Int): FFmpegBuilder {
    this.pass = pass
    return this
  }

  fun setPassDirectory(directory: String): FFmpegBuilder {
    this.pass_directory = Preconditions.checkNotNull(directory)
    return this
  }

  fun setPassPrefix(prefix: String): FFmpegBuilder {
    this.pass_prefix = Preconditions.checkNotNull(prefix)
    return this
  }

  fun setVerbosity(verbosity: Verbosity): FFmpegBuilder {
    this.verbosity = Preconditions.checkNotNull(verbosity)
    return this
  }

  fun setUserAgent(userAgent: String): FFmpegBuilder {
    this.user_agent = Preconditions.checkNotNull(userAgent)
    return this
  }

  @Deprecated("Use AbstractFFmpegInputBuilder.readAtNativeFrameRate() instead")
  fun readAtNativeFrameRate(): FFmpegBuilder {
    this.read_at_native_frame_rate = true
    return this
  }

  fun addInput(result: FFmpegProbeResult): FFmpegFileInputBuilder {
    Preconditions.checkNotNull(result)
    val format = Preconditions.checkNotNull(result.format) { "ProbeResult format is null" }
    val filename = Preconditions.checkNotNull(format.filename) { "ProbeResult filename is null" }
    return doAddInput(FFmpegFileInputBuilder(this, filename, result))
  }

  fun addInput(filename: String): FFmpegFileInputBuilder {
    Preconditions.checkNotNull(filename)
    return doAddInput(FFmpegFileInputBuilder(this, filename))
  }
  
  fun <T : AbstractFFmpegInputBuilder<T>> addInput(input: T) : FFmpegBuilder {
    // Assuming AbstractFFmpegInputBuilder and its subtypes have a "done()" method that returns FFmpegBuilder
    // This might require casting or adjusting "done()" if it's not directly returning FFmpegBuilder
    // For now, let's assume "done()" is part of the fluent interface returning to parent.
    // However, AbstractFFmpegStreamBuilder.done() returns FFmpegBuilder, so this should be fine.
    return doAddInput(input).done() 
  }

  protected fun <T : AbstractFFmpegInputBuilder<*>> doAddInput(input: T): T {
    Preconditions.checkNotNull(input)
    inputs.add(input)
    return input
  }

  protected fun clearInputs() {
    inputs.clear()
    // inputProbes.clear()
  }

  fun setInput(result: FFmpegProbeResult): FFmpegFileInputBuilder {
    clearInputs()
    return addInput(result)
  }

  fun setInput(filename: String): FFmpegFileInputBuilder {
    clearInputs()
    return addInput(filename)
  }

  fun <T : AbstractFFmpegInputBuilder<T>> setInput(input: T): FFmpegBuilder {
    Preconditions.checkNotNull(input)
    clearInputs()
    inputs.add(input)
    return this
  }

  fun setThreads(threads: Int): FFmpegBuilder {
    require(threads > 0) { "threads must be greater than zero" }
    this.threads = threads
    return this
  }

  @Deprecated("Specify this option on an input stream using AbstractFFmpegStreamBuilder.setFormat(String)")
  fun setFormat(format: String): FFmpegBuilder {
    this.format = Preconditions.checkNotNull(format)
    return this
  }

  @Deprecated("Specify this option on an input or output stream using AbstractFFmpegStreamBuilder.setStartOffset(long, TimeUnit)")
  fun setStartOffset(duration: Long, units: TimeUnit): FFmpegBuilder {
    this.startOffset = units.toMillis(duration)
    return this
  }

  fun addProgress(uri: URI): FFmpegBuilder {
    this.progress = Preconditions.checkNotNull(uri)
    return this
  }

  @Deprecated("Use AbstractFFmpegOutputBuilder.setComplexFilter(String) instead")
  fun setComplexFilter(filter: String): FFmpegBuilder {
    this.complexFilter = FfmpegPreconditions.checkNotEmpty(filter, "filter must not be empty")
    return this
  }

  fun setAudioFilter(filter: String): FFmpegBuilder {
    this.audioFilter = FfmpegPreconditions.checkNotEmpty(filter, "filter must not be empty")
    return this
  }

  fun setVideoFilter(filter: String): FFmpegBuilder {
    this.videoFilter = FfmpegPreconditions.checkNotEmpty(filter, "filter must not be empty")
    return this
  }
  
  fun setVBR(quality: Int): FFmpegBuilder {
    Preconditions.checkArgument(quality >= 0 && quality <= 9, "vbr must be between 0 and 9")
    this.qscale = quality
    return this
  }

  fun addExtraArgs(vararg values: String): FFmpegBuilder {
    require(values.isNotEmpty()) { "one or more values must be supplied" }
    FfmpegPreconditions.checkNotEmpty(values[0], "first extra arg may not be empty")
    for (value in values) {
      extra_args.add(Preconditions.checkNotNull(value))
    }
    return this
  }

  fun addOutput(filename: String): FFmpegOutputBuilder {
    val output = FFmpegOutputBuilder(this, filename)
    outputs.add(output)
    return output
  }

  fun addOutput(uri: URI): FFmpegOutputBuilder {
    val output = FFmpegOutputBuilder(this, uri)
    outputs.add(output)
    return output
  }

  fun addHlsOutput(filename: String): FFmpegHlsOutputBuilder {
    val output = FFmpegHlsOutputBuilder(this, filename)
    outputs.add(output)
    return output
  }
  
  fun addOutput(output: AbstractFFmpegOutputBuilder<*>): FFmpegBuilder { // More generic type
    outputs.add(output)
    return this
  }

  fun addStdoutOutput(): FFmpegOutputBuilder {
    return addOutput("-")
  }

  fun build(): List<String> {
    val args = ImmutableList.builder<String>()

    Preconditions.checkArgument(inputs.isNotEmpty(), "At least one input must be specified")
    Preconditions.checkArgument(outputs.isNotEmpty(), "At least one output must be specified")

    if (strict != Strict.NORMAL) {
      args.add("-strict", strict.toString().lowercase()) // Assuming Strict.toString() is appropriate or use .name
    }

    args.add(if (override) "-y" else "-n")
    args.add("-v", this.verbosity.toString())

    user_agent?.let { args.add("-user_agent", it) }

    if (startOffset != null) {
      log.warn("Using FFmpegBuilder#setStartOffset is deprecated. Specify it on the inputStream or outputStream instead")
      args.add("-ss", FFmpegUtils.toTimecode(startOffset!!, TimeUnit.MILLISECONDS))
    }

    if (threads > 0) {
      args.add("-threads", threads.toString())
    }

    if (format != null) {
      log.warn("Using FFmpegBuilder#setFormat is deprecated. Specify it on the inputStream or outputStream instead")
      args.add("-f", format)
    }

    if (read_at_native_frame_rate) {
      log.warn("Using FFmpegBuilder#readAtNativeFrameRate is deprecated. Specify it on the inputStream instead")
      args.add("-re")
    }

    progress?.let { args.add("-progress", it.toString()) }
    args.addAll(extra_args)

    for (input in this.inputs) {
      args.addAll(input.build(this, pass))
    }

    if (pass > 0) {
      args.add("-pass", pass.toString())
      pass_prefix?.let { args.add("-passlogfile", pass_directory + it) }
    }

    if (!Strings.isNullOrEmpty(audioFilter)) {
      args.add("-af", audioFilter)
    }

    if (!Strings.isNullOrEmpty(videoFilter)) {
      args.add("-vf", videoFilter)
    }

    if (!Strings.isNullOrEmpty(complexFilter)) {
      log.warn("Using FFmpegBuilder#setComplexFilter is deprecated. Specify it on the outputStream instead")
      args.add("-filter_complex", complexFilter)
    }
    
    qscale?.let { args.add("-qscale:a", it.toString()) }

    for (output in this.outputs) {
      args.addAll(output.build(this, pass))
    }

    return args.build()
  }

  companion object {
    private val log: Logger = LoggerFactory.getLogger(FFmpegBuilder::class.java)
  }
}
