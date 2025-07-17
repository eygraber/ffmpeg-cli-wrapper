package net.bramp.ffmpeg.builder

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.options.AudioEncodingOptions
import net.bramp.ffmpeg.options.EncodingOptions
import net.bramp.ffmpeg.options.MainEncodingOptions
import net.bramp.ffmpeg.options.VideoEncodingOptions
import java.net.URI
import java.util.regex.Pattern

/** Builds a representation of a single output/encoding setting */
abstract class AbstractFFmpegOutputBuilder<T : AbstractFFmpegOutputBuilder<T>> :
  AbstractFFmpegStreamBuilder<T> {

  // Direct mapping of deprecated public fields to public var properties with @Deprecated
  // This maintains source compatibility for Java users to some extent,
  // though direct field access from Java might behave differently than properties.
  // For Kotlin users, they are properties.
  @Deprecated("Use getConstantRateFactor() or setConstantRateFactor()")
  var constantRateFactor: Double? = null

  @Deprecated("Use getAudioSampleFormat() or setAudioSampleFormat()")
  var audio_sample_format: String? = null

  @Deprecated("Use getAudioBitRate() or setAudioBitRate()")
  var audio_bit_rate: Long = 0L

  @Deprecated("Use getAudioQuality() or setAudioQuality()")
  var audio_quality: Double? = null

  @Deprecated("Use getAudioBitStreamFilter() or setAudioBitStreamFilter()")
  var audio_bit_stream_filter: String? = null

  @Deprecated("Use getAudioFilter() or setAudioFilter()")
  var audio_filter: String? = null

  @Deprecated("Use getVideoBitRate() or setVideoBitRate()")
  var video_bit_rate: Long = 0L

  @Deprecated("Use getVideoQuality() or setVideoQuality()")
  var video_quality: Double? = null

  @Deprecated("Use getVideoPreset() or setVideoPreset()")
  var video_preset: String? = null

  @Deprecated("Use getVideoFilter() or setVideoFilter()")
  var video_filter: String? = null

  @Deprecated("Use getVideoBitStreamFilter() or setVideoBitStreamFilter()")
  var video_bit_stream_filter: String? = null

  protected var bFrames: Int? = null

  @JvmField
  protected var complexFilter: String? = null

  constructor() : super()

  protected constructor(parent: FFmpegBuilder, filename: String) : super(parent, filename)

  protected constructor(parent: FFmpegBuilder, uri: URI) : super(parent, uri)

  fun setConstantRateFactor(factor: Double): T {
    require(factor >= 0) { "constant rate factor must be greater or equal to zero" }
    this.constantRateFactor = factor
    return getThis()
  }

  fun setVideoBitRate(bit_rate: Long): T {
    require(bit_rate > 0) { "bit rate must be positive" }
    this.video_enabled = true
    this.video_bit_rate = bit_rate
    return getThis()
  }

  fun setVideoQuality(quality: Double): T {
    require(quality > 0) { "quality must be positive" }
    this.video_enabled = true
    this.video_quality = quality
    return getThis()
  }

  fun setVideoBitStreamFilter(filter: String): T {
    this.video_bit_stream_filter = net.bramp.ffmpeg.Preconditions.checkNotEmpty(filter, "filter must not be empty")
    return getThis()
  }

  fun setVideoPreset(preset: String): T {
    this.video_enabled = true
    this.video_preset = net.bramp.ffmpeg.Preconditions.checkNotEmpty(preset, "video preset must not be empty")
    return getThis()
  }

  fun setBFrames(bFrames: Int): T {
    this.bFrames = bFrames
    return getThis()
  }

  fun setVideoFilter(filter: String): T {
    this.video_enabled = true
    this.video_filter = net.bramp.ffmpeg.Preconditions.checkNotEmpty(filter, "filter must not be empty")
    return getThis()
  }

  @Deprecated(
    "use setAudioSampleFormat instead.",
    ReplaceWith("this.setAudioSampleFormat(bit_depth)"),
  )
  fun setAudioBitDepth(bit_depth: String): T = setAudioSampleFormat(bit_depth)

  fun setAudioSampleFormat(sample_format: String): T {
    this.audio_enabled = true
    this.audio_sample_format =
      net.bramp.ffmpeg.Preconditions.checkNotEmpty(sample_format, "sample format must not be empty")
    return getThis()
  }

  fun setAudioBitRate(bit_rate: Long): T {
    require(bit_rate > 0) { "bit rate must be positive" }
    this.audio_enabled = true
    this.audio_bit_rate = bit_rate
    return getThis()
  }

  fun setAudioQuality(quality: Double): T {
    require(quality > 0) { "quality must be positive" }
    this.audio_enabled = true
    this.audio_quality = quality
    return getThis()
  }

  fun setAudioBitStreamFilter(filter: String): T {
    this.audio_enabled = true
    this.audio_bit_stream_filter = net.bramp.ffmpeg.Preconditions.checkNotEmpty(filter, "filter must not be empty")
    return getThis()
  }

  fun setComplexFilter(filter: String): T {
    this.complexFilter = net.bramp.ffmpeg.Preconditions.checkNotEmpty(filter, "filter must not be empty")
    return getThis()
  }

  fun setAudioFilter(filter: String): T {
    this.audio_enabled = true
    this.audio_filter = net.bramp.ffmpeg.Preconditions.checkNotEmpty(filter, "filter must not be empty")
    return getThis()
  }

  override fun buildOptions(): EncodingOptions = EncodingOptions(
    MainEncodingOptions(format, startOffset, duration),
    AudioEncodingOptions(
      audio_enabled,
      audio_codec,
      audio_channels,
      audio_sample_rate,
      audio_sample_format,
      audio_bit_rate,
      audio_quality,
    ),
    VideoEncodingOptions(
      video_enabled,
      video_codec,
      video_frame_rate,
      video_width,
      video_height,
      video_bit_rate,
      video_frames,
      video_filter,
      video_preset,
    ),
  )

  override fun build(pass: Int): List<String> {
    Preconditions.checkState(parent != null, "Can not build without parent being set")
    return build(parent!!, pass)
  }

  override fun build(parent: FFmpegBuilder, pass: Int): List<String> {
    if(pass > 0) {
      require(targetSize != 0L || video_bit_rate != 0L) {
        "Target size, or video bitrate must be specified when using two-pass"
      }
      require(format != null) { "Format must be specified when using two-pass" }
    }

    if(targetSize > 0L) {
      check(parent.inputs.size == 1) { "Target size does not support multiple inputs" }
      require(constantRateFactor == null) {
        "Target size can not be used with constantRateFactor"
      }

      val firstInput = parent.inputs.first() // Assuming inputs is not empty
      val inputProbeResult = firstInput.probeResult
      checkNotNull(inputProbeResult) { "Target size must be used with setInput(FFmpegProbeResult)" }

      val durationInSeconds = inputProbeResult.format?.duration ?: 0.0
      val totalBitRate = ((targetSize * 8) / durationInSeconds).toLong() - pass_padding_bitrate

      if(video_enabled && video_bit_rate == 0L) {
        val audioBitRateForCalc = if(audio_enabled) audio_bit_rate else 0L
        video_bit_rate = totalBitRate - audioBitRateForCalc
      } else if(audio_enabled && audio_bit_rate == 0L) {
        audio_bit_rate = totalBitRate
      }
    }
    return super.build(parent, pass)
  }

  override fun addGlobalFlags(parent: FFmpegBuilder, args: ImmutableList.Builder<String>) {
    super.addGlobalFlags(parent, args)
    constantRateFactor?.let { args.add("-crf", formatDecimalInteger(it)) }
    complexFilter?.let { args.add("-filter_complex", it) }
  }

  override fun addVideoFlags(parent: FFmpegBuilder, args: ImmutableList.Builder<String>) {
    super.addVideoFlags(parent, args)

    if(video_bit_rate > 0L && video_quality != null) {
      throw IllegalStateException("Only one of video_bit_rate and video_quality can be set")
    }
    if(video_bit_rate > 0L) {
      args.add("-b:v", video_bit_rate.toString())
    }
    video_quality?.let { args.add("-qscale:v", formatDecimalInteger(it)) }
    if(!Strings.isNullOrEmpty(video_preset)) {
      args.add("-vpre", video_preset)
    }
    if(!Strings.isNullOrEmpty(video_filter)) {
      check(parent.inputs.size == 1) {
        "Video filter only works with one input, instead use setComplexVideoFilter(..)"
      }
      args.add("-vf", video_filter)
    }
    if(!Strings.isNullOrEmpty(video_bit_stream_filter)) {
      args.add("-bsf:v", video_bit_stream_filter)
    }
    bFrames?.let { args.add("-bf", it.toString()) }
  }

  override fun addAudioFlags(args: ImmutableList.Builder<String>) {
    super.addAudioFlags(args)
    if(!Strings.isNullOrEmpty(audio_sample_format)) {
      args.add("-sample_fmt", audio_sample_format)
    }
    if(audio_bit_rate > 0L && audio_quality != null && throwWarnings) { // throwWarnings is inherited
      throw IllegalStateException("Only one of audio_bit_rate and audio_quality can be set")
    }
    if(audio_bit_rate > 0L) {
      args.add("-b:a", audio_bit_rate.toString())
    }
    audio_quality?.let { args.add("-qscale:a", formatDecimalInteger(it)) }
    if(!Strings.isNullOrEmpty(audio_bit_stream_filter)) {
      args.add("-bsf:a", audio_bit_stream_filter)
    }
    if(!Strings.isNullOrEmpty(audio_filter)) {
      args.add("-af", audio_filter)
    }
  }

  override fun addSourceTarget(pass: Int, args: ImmutableList.Builder<String>) {
    if(filename != null && uri != null) {
      throw IllegalStateException("Only one of filename and uri can be set")
    }
    when {
      pass == 1 -> args.add(DEVNULL) // DEVNULL assumed to be inherited or defined
      filename != null -> args.add(filename)
      uri != null -> args.add(uri.toString())
      else -> assert(false) { "Either filename or uri must be set, or pass must be 1" }
    }
  }

  // getThis() is assumed to be inherited from AbstractFFmpegStreamBuilder.kt

  companion object {
    internal val trailingZero: Pattern = Pattern.compile("\\.0*$")

    @JvmStatic
    fun formatDecimalInteger(d: Double): String = trailingZero.matcher(d.toString()).replaceAll("")
  }
}
