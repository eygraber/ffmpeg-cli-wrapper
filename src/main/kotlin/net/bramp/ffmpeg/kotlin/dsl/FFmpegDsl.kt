package net.bramp.ffmpeg.kotlin.dsl

import net.bramp.ffmpeg.kotlin.FFmpegExecutor
import net.bramp.ffmpeg.kotlin.builder.FFmpegBuilder
import net.bramp.ffmpeg.kotlin.builder.FFmpegFileInputBuilder
import net.bramp.ffmpeg.kotlin.builder.FFmpegHlsOutputBuilder
import net.bramp.ffmpeg.kotlin.builder.FFmpegOutputBuilder
import net.bramp.ffmpeg.kotlin.builder.Strict
import net.bramp.ffmpeg.kotlin.probe.FFmpegProbeResult
import org.apache.commons.lang3.math.Fraction
import java.net.URI
import java.util.concurrent.TimeUnit

/**
 * DSL marker for FFmpeg builder functions
 */
@DslMarker
annotation class FFmpegDslMarker

/**
 * Creates an FFmpegBuilder using a Kotlin DSL.
 *
 * Example:
 * ```
 * val builder = ffmpeg {
 *   input("input.mp4") {
 *     startOffset(1500, TimeUnit.MILLISECONDS)
 *   }
 *   output("output.mp4") {
 *     format = "mp4"
 *     videoCodec = "libx264"
 *     videoResolution(640, 480)
 *     audioCodec = "aac"
 *     audioChannels = 1
 *   }
 * }
 * ```
 */
fun ffmpeg(block: FFmpegDsl.() -> Unit): FFmpegBuilder {
  val dsl = FFmpegDsl()
  dsl.block()
  return dsl.builder
}

/**
 * Creates and executes an FFmpeg job using a Kotlin DSL.
 *
 * Example:
 * ```
 * val ffmpeg = FFmpeg()
 * val ffprobe = FFprobe()
 * val executor = FFmpegExecutor(ffmpeg, ffprobe)
 *
 * executor.execute {
 *   input("input.mp4")
 *   output("output.mp4") {
 *     videoCodec = "libx264"
 *   }
 * }
 * ```
 */
fun FFmpegExecutor.execute(block: FFmpegDsl.() -> Unit) {
  val builder = ffmpeg(block)
  createJob(builder).run()
}

/**
 * Main DSL class for building FFmpeg commands
 */
@FFmpegDslMarker
@Suppress("BooleanPropertyNaming")
class FFmpegDsl {
  internal val builder = FFmpegBuilder()

  /**
   * Override output files (default: true)
   */
  var overrideOutputFiles: Boolean
    get() = builder.overrideOutputFiles
    set(value) {
      builder.overrideOutputFiles(value)
    }

  /**
   * Set verbosity level
   */
  var verbosity: FFmpegBuilder.Verbosity = FFmpegBuilder.Verbosity.Error
    set(value) {
      field = value
      builder.setVerbosity(value)
    }

  /**
   * Set user agent string
   */
  var userAgent: String? = null
    set(value) {
      field = value
      value?.let { builder.setUserAgent(it) }
    }

  /**
   * Set encoding pass (for two-pass encoding)
   */
  var pass: Int = 0
    set(value) {
      field = value
      builder.setPass(value)
    }

  /**
   * Set pass directory
   */
  var passDirectory: String = ""
    set(value) {
      field = value
      builder.setPassDirectory(value)
    }

  /**
   * Set pass prefix
   */
  var passPrefix: String? = null
    set(value) {
      field = value
      value?.let { builder.setPassPrefix(it) }
    }

  /**
   * Set strict mode
   */
  var strict: Strict = Strict.Normal
    set(value) {
      field = value
      builder.setStrict(value)
    }

  /**
   * Set number of threads
   */
  var threads: Int = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setThreads(value)
      }
    }

  /**
   * Set audio filter
   */
  var audioFilter: String? = null
    set(value) {
      field = value
      value?.let { builder.setAudioFilter(it) }
    }

  /**
   * Set video filter
   */
  var videoFilter: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoFilter(it) }
    }

  /**
   * Add an input file with optional configuration
   */
  fun input(filename: String, block: InputDsl.() -> Unit = {}) {
    val inputBuilder = builder.addInput(filename)
    val dsl = InputDsl(inputBuilder)
    dsl.block()
    inputBuilder.done()
  }

  /**
   * Add an input file from a probe result with optional configuration
   */
  fun input(probeResult: FFmpegProbeResult, block: InputDsl.() -> Unit = {}) {
    val inputBuilder = builder.addInput(probeResult)
    val dsl = InputDsl(inputBuilder)
    dsl.block()
    inputBuilder.done()
  }

  /**
   * Add an output file with optional configuration
   */
  fun output(filename: String, block: OutputDsl.() -> Unit = {}) {
    val outputBuilder = builder.addOutput(filename)
    val dsl = OutputDsl(outputBuilder)
    dsl.block()
    outputBuilder.done()
  }

  /**
   * Add an output URI with optional configuration
   */
  fun output(uri: URI, block: OutputDsl.() -> Unit = {}) {
    val outputBuilder = builder.addOutput(uri)
    val dsl = OutputDsl(outputBuilder)
    dsl.block()
    outputBuilder.done()
  }

  /**
   * Add an HLS output with optional configuration
   */
  fun hlsOutput(filename: String, block: HlsOutputDsl.() -> Unit = {}) {
    val outputBuilder = builder.addHlsOutput(filename)
    val dsl = HlsOutputDsl(outputBuilder)
    dsl.block()
    outputBuilder.done()
  }

  /**
   * Add an output to stdout
   */
  fun stdoutOutput(block: OutputDsl.() -> Unit = {}) {
    val outputBuilder = builder.addStdoutOutput()
    val dsl = OutputDsl(outputBuilder)
    dsl.block()
    outputBuilder.done()
  }

  /**
   * Add extra arguments
   */
  fun extraArgs(vararg args: String) {
    builder.addExtraArgs(*args)
  }

  /**
   * Add progress listener
   */
  fun progress(uri: URI) {
    builder.addProgress(uri)
  }

  /**
   * Set VBR quality (0-9, where 0 is best)
   */
  fun vbr(quality: Int) {
    builder.setVBR(quality)
  }
}

/**
 * DSL for configuring input streams
 */
@FFmpegDslMarker
class InputDsl(private val builder: FFmpegFileInputBuilder) {
  /**
   * Set input format
   */
  var format: String? = null
    set(value) {
      field = value
      value?.let { builder.setFormat(it) }
    }

  /**
   * Set stream loop count (-1 for infinite)
   */
  var streamLoop: Int = 0
    set(value) {
      field = value
      builder.setStreamLoop(value)
    }

  /**
   * Set start offset
   */
  fun startOffset(duration: Long, units: TimeUnit) {
    builder.setStartOffset(duration, units)
  }

  /**
   * Set duration
   */
  fun duration(duration: Long, units: TimeUnit) {
    builder.setDuration(duration, units)
  }

  /**
   * Read at native frame rate
   */
  fun readAtNativeFrameRate() {
    builder.readAtNativeFrameRate()
  }

  /**
   * Add extra arguments
   */
  fun extraArgs(vararg args: String) {
    builder.addExtraArgs(*args)
  }

  /**
   * Set video resolution
   */
  fun videoResolution(width: Int, height: Int) {
    builder.setVideoResolution(width, height)
  }

  /**
   * Set video frame rate
   */
  fun videoFrameRate(rate: Double) {
    builder.setVideoFrameRate(rate)
  }

  /**
   * Set video frame rate as fraction
   */
  fun videoFrameRate(frames: Int, per: Int) {
    builder.setVideoFrameRate(frames, per)
  }
}

/**
 * DSL for configuring output streams
 */
@FFmpegDslMarker
class OutputDsl(private val builder: FFmpegOutputBuilder) {
  /**
   * Set output format
   */
  var format: String? = null
    set(value) {
      field = value
      value?.let { builder.setFormat(it) }
    }

  /**
   * Set target size in bytes
   */
  var targetSize: Long = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setTargetSize(value)
      }
    }

  // Video settings

  /**
   * Set video codec
   */
  var videoCodec: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoCodec(it) }
    }

  /**
   * Set video frame rate as Fraction
   */
  var videoFrameRate: Fraction? = null
    set(value) {
      field = value
      value?.let { builder.setVideoFrameRate(it) }
    }

  /**
   * Set video bit rate
   */
  var videoBitRate: Long = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setVideoBitRate(value)
      }
    }

  /**
   * Set video quality
   */
  var videoQuality: Double? = null
    set(value) {
      field = value
      value?.let { builder.setVideoQuality(it) }
    }

  /**
   * Set video preset
   */
  var videoPreset: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoPreset(it) }
    }

  /**
   * Set video filter
   */
  var videoFilter: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoFilter(it) }
    }

  /**
   * Set video bit stream filter
   */
  var videoBitStreamFilter: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoBitStreamFilter(it) }
    }

  /**
   * Set constant rate factor
   */
  var constantRateFactor: Double? = null
    set(value) {
      field = value
      value?.let { builder.setConstantRateFactor(it) }
    }

  /**
   * Set video pixel format
   */
  var videoPixelFormat: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoPixelFormat(it) }
    }

  /**
   * Set number of B-frames
   */
  var bFrames: Int? = null
    set(value) {
      field = value
      value?.let { builder.setBFrames(it) }
    }

  /**
   * Set video movflags
   */
  var videoMovFlags: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoMovFlags(it) }
    }

  /**
   * Set number of video frames
   */
  var frames: Int? = null
    set(value) {
      field = value
      value?.let { builder.setFrames(it) }
    }

  // Audio settings

  /**
   * Set audio codec
   */
  var audioCodec: String? = null
    set(value) {
      field = value
      value?.let { builder.setAudioCodec(it) }
    }

  /**
   * Set audio channels
   */
  var audioChannels: Int = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setAudioChannels(value)
      }
    }

  /**
   * Set audio sample rate
   */
  var audioSampleRate: Int = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setAudioSampleRate(value)
      }
    }

  /**
   * Set audio sample format
   */
  var audioSampleFormat: String? = null
    set(value) {
      field = value
      value?.let { builder.setAudioSampleFormat(it) }
    }

  /**
   * Set audio bit rate
   */
  var audioBitRate: Long = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setAudioBitRate(value)
      }
    }

  /**
   * Set audio quality
   */
  var audioQuality: Double? = null
    set(value) {
      field = value
      value?.let { builder.setAudioQuality(it) }
    }

  /**
   * Set audio bit stream filter
   */
  var audioBitStreamFilter: String? = null
    set(value) {
      field = value
      value?.let { builder.setAudioBitStreamFilter(it) }
    }

  /**
   * Set audio filter
   */
  var audioFilter: String? = null
    set(value) {
      field = value
      value?.let { builder.setAudioFilter(it) }
    }

  // Subtitle settings

  /**
   * Set subtitle codec
   */
  var subtitleCodec: String? = null
    set(value) {
      field = value
      value?.let { builder.setSubtitleCodec(it) }
    }

  // Presets

  /**
   * Set preset
   */
  var preset: String? = null
    set(value) {
      field = value
      value?.let { builder.setPreset(it) }
    }

  /**
   * Set preset filename
   */
  var presetFilename: String? = null
    set(value) {
      field = value
      value?.let { builder.setPresetFilename(it) }
    }

  /**
   * Set video preset
   */
  var videoPresetFile: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoPreset(it) }
    }

  /**
   * Set audio preset
   */
  var audioPreset: String? = null
    set(value) {
      field = value
      value?.let { builder.setAudioPreset(it) }
    }

  /**
   * Set subtitle preset
   */
  var subtitlePreset: String? = null
    set(value) {
      field = value
      value?.let { builder.setSubtitlePreset(it) }
    }

  /**
   * Set complex filter
   */
  var complexFilter: String? = null
    set(value) {
      field = value
      value?.let { builder.setComplexFilter(it) }
    }

  /**
   * Set strict mode
   */
  var strict: Strict = Strict.Normal
    set(value) {
      field = value
      builder.setStrict(value)
    }

  /**
   * Set start offset
   */
  fun startOffset(duration: Long, units: TimeUnit) {
    builder.setStartOffset(duration, units)
  }

  /**
   * Set duration
   */
  fun duration(duration: Long, units: TimeUnit) {
    builder.setDuration(duration, units)
  }

  /**
   * Set video resolution
   */
  fun videoResolution(width: Int, height: Int) {
    builder.setVideoResolution(width, height)
  }

  /**
   * Set video resolution by abbreviation (e.g., "ntsc", "hd720")
   */
  fun videoResolution(abbreviation: String) {
    builder.setVideoResolution(abbreviation)
  }

  /**
   * Set video frame rate
   */
  fun videoFrameRate(rate: Double) {
    builder.setVideoFrameRate(rate)
  }

  /**
   * Set video frame rate as fraction
   */
  fun videoFrameRate(frames: Int, per: Int) {
    builder.setVideoFrameRate(frames, per)
  }

  /**
   * Disable video
   */
  fun disableVideo() {
    builder.disableVideo()
  }

  /**
   * Disable audio
   */
  fun disableAudio() {
    builder.disableAudio()
  }

  /**
   * Disable subtitles
   */
  fun disableSubtitle() {
    builder.disableSubtitle()
  }

  /**
   * Add metadata tag
   */
  fun metadata(key: String, value: String) {
    builder.addMetaTag(key, value)
  }

  /**
   * Add extra arguments
   */
  fun extraArgs(vararg args: String) {
    builder.addExtraArgs(*args)
  }
}

/**
 * DSL for configuring HLS output streams
 */
@FFmpegDslMarker
class HlsOutputDsl(private val builder: FFmpegHlsOutputBuilder) {
  /**
   * Set target size in bytes
   */
  var targetSize: Long = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setTargetSize(value)
      }
    }

  // HLS-specific settings

  /**
   * Set HLS segment filename pattern
   */
  var hlsSegmentFilename: String? = null
    set(value) {
      field = value
      value?.let { builder.setHlsSegmentFileName(it) }
    }

  /**
   * Set HLS list size
   */
  var hlsListSize: Int? = null
    set(value) {
      field = value
      value?.let { builder.setHlsListSize(it) }
    }

  /**
   * Set HLS base URL
   */
  var hlsBaseUrl: String? = null
    set(value) {
      field = value
      value?.let { builder.setHlsBaseUrl(it) }
    }

  // Video settings

  /**
   * Set video codec
   */
  var videoCodec: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoCodec(it) }
    }

  /**
   * Set video bit rate
   */
  var videoBitRate: Long = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setVideoBitRate(value)
      }
    }

  /**
   * Set video preset
   */
  var videoPreset: String? = null
    set(value) {
      field = value
      value?.let { builder.setVideoPreset(it) }
    }

  // Audio settings

  /**
   * Set audio codec
   */
  var audioCodec: String? = null
    set(value) {
      field = value
      value?.let { builder.setAudioCodec(it) }
    }

  /**
   * Set audio channels
   */
  var audioChannels: Int = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setAudioChannels(value)
      }
    }

  /**
   * Set audio sample rate
   */
  var audioSampleRate: Int = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setAudioSampleRate(value)
      }
    }

  /**
   * Set audio bit rate
   */
  var audioBitRate: Long = 0
    set(value) {
      field = value
      if(value > 0) {
        builder.setAudioBitRate(value)
      }
    }

  /**
   * Set start offset
   */
  fun startOffset(duration: Long, units: TimeUnit) {
    builder.setStartOffset(duration, units)
  }

  /**
   * Set duration
   */
  fun duration(duration: Long, units: TimeUnit) {
    builder.setDuration(duration, units)
  }

  /**
   * Set HLS segment time
   */
  fun hlsTime(duration: Long, units: TimeUnit) {
    builder.setHlsTime(duration, units)
  }

  /**
   * Set HLS init time
   */
  fun hlsInitTime(duration: Long, units: TimeUnit) {
    builder.setHlsInitTime(duration, units)
  }

  /**
   * Set video resolution
   */
  fun videoResolution(width: Int, height: Int) {
    builder.setVideoResolution(width, height)
  }

  /**
   * Set video frame rate
   */
  fun videoFrameRate(rate: Double) {
    builder.setVideoFrameRate(rate)
  }

  /**
   * Set video frame rate as fraction
   */
  fun videoFrameRate(frames: Int, per: Int) {
    builder.setVideoFrameRate(frames, per)
  }

  /**
   * Disable video
   */
  fun disableVideo() {
    builder.disableVideo()
  }

  /**
   * Disable audio
   */
  fun disableAudio() {
    builder.disableAudio()
  }

  /**
   * Add extra arguments
   */
  fun extraArgs(vararg args: String) {
    builder.addExtraArgs(*args)
  }
}
