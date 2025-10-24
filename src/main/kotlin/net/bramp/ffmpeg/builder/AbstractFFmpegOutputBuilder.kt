package net.bramp.ffmpeg.builder

import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.options.AudioEncodingOptions
import net.bramp.ffmpeg.options.EncodingOptions
import net.bramp.ffmpeg.options.MainEncodingOptions
import net.bramp.ffmpeg.options.VideoEncodingOptions
import java.net.URI
import java.util.regex.Pattern

/** Builds a representation of a single output/encoding setting */
@Suppress("Deprecation")
abstract class AbstractFFmpegOutputBuilder<T : AbstractFFmpegOutputBuilder<T>> :
  AbstractFFmpegStreamBuilder<T> {

  // Direct mapping of deprecated public fields to public var properties with @Deprecated
  // This maintains source compatibility for Java users to some extent,
  // though direct field access from Java might behave differently than properties.
  // For Kotlin users, they are properties.
  @Deprecated("Use getConstantRateFactor() or setConstantRateFactor()")
  var constantRateFactor: Double? = null

  @Deprecated("Use getAudioSampleFormat() or setAudioSampleFormat()")
  @JvmField
  var audioSampleFormat: String? = null

  @Deprecated("Use getAudioBitRate() or setAudioBitRate()")
  @JvmField
  var audioBitRate: Long = 0L

  @Deprecated("Use getAudioQuality() or setAudioQuality()")
  var audioQuality: Double? = null

  @Deprecated("Use getAudioBitStreamFilter() or setAudioBitStreamFilter()")
  @JvmField
  var audioBitStreamFilter: String? = null

  @Deprecated("Use getAudioFilter() or setAudioFilter()")
  @JvmField
  var audioFilter: String? = null

  @Deprecated("Use getVideoBitRate() or setVideoBitRate()")
  @JvmField
  var videoBitRate: Long = 0L

  @Deprecated("Use getVideoQuality() or setVideoQuality()")
  var videoQuality: Double? = null

  @Deprecated("Use getVideoPreset() or setVideoPreset()")
  @JvmField
  var videoPreset: String? = null

  @Deprecated("Use getVideoFilter() or setVideoFilter()")
  @JvmField
  var videoFilter: String? = null

  @Deprecated("Use getVideoBitStreamFilter() or setVideoBitStreamFilter()")
  @JvmField
  var videoBitStreamFilter: String? = null

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

  fun setVideoBitRate(bitRate: Long): T {
    require(bitRate > 0) { "bit rate must be positive" }
    this.video_enabled = true
    this.videoBitRate = bitRate
    return getThis()
  }

  fun setVideoQuality(quality: Double): T {
    require(quality > 0) { "quality must be positive" }
    this.video_enabled = true
    this.videoQuality = quality
    return getThis()
  }

  fun setVideoBitStreamFilter(filter: String): T {
    this.videoBitStreamFilter =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(filter, "filter must not be empty")
    return getThis()
  }

  fun setVideoPreset(preset: String): T {
    this.video_enabled = true
    this.videoPreset = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(preset, "video preset must not be empty")
    return getThis()
  }

  fun setBFrames(bFrames: Int): T {
    this.bFrames = bFrames
    return getThis()
  }

  fun setVideoFilter(filter: String): T {
    this.video_enabled = true
    this.videoFilter = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(filter, "filter must not be empty")
    return getThis()
  }

  @Deprecated(
    "use setAudioSampleFormat instead.",
    ReplaceWith("this.setAudioSampleFormat(bitDepth)"),
  )
  fun setAudioBitDepth(bitDepth: String): T = setAudioSampleFormat(bitDepth)

  fun setAudioSampleFormat(sampleFormat: String): T {
    this.audio_enabled = true
    this.audioSampleFormat =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(sampleFormat, "sample format must not be empty")
    return getThis()
  }

  fun setAudioBitRate(bitRate: Long): T {
    require(bitRate > 0) { "bit rate must be positive" }
    this.audio_enabled = true
    this.audioBitRate = bitRate
    return getThis()
  }

  fun setAudioQuality(quality: Double): T {
    require(quality > 0) { "quality must be positive" }
    this.audio_enabled = true
    this.audioQuality = quality
    return getThis()
  }

  fun setAudioBitStreamFilter(filter: String): T {
    this.audio_enabled = true
    this.audioBitStreamFilter =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(filter, "filter must not be empty")
    return getThis()
  }

  fun setComplexFilter(filter: String): T {
    this.complexFilter = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(filter, "filter must not be empty")
    return getThis()
  }

  fun setAudioFilter(filter: String): T {
    this.audio_enabled = true
    this.audioFilter = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(filter, "filter must not be empty")
    return getThis()
  }

  override fun buildOptions(): EncodingOptions = EncodingOptions(
    MainEncodingOptions(format, startOffset, duration),
    AudioEncodingOptions(
      isEnabled = audio_enabled,
      codec = audio_codec,
      channels = audio_channels,
      sampleRate = audio_sample_rate,
      sampleFormat = audioSampleFormat,
      bitRate = audioBitRate,
      quality = audioQuality,
    ),
    VideoEncodingOptions(
      isEnabled = video_enabled,
      codec = video_codec,
      frameRate = video_frame_rate,
      width = video_width,
      height = video_height,
      bitRate = videoBitRate,
      frames = video_frames,
      filter = videoFilter,
      preset = videoPreset,
    ),
  )

  override fun useOptions(opts: AudioEncodingOptions): T {
    super.useOptions(opts)
    opts.sampleFormat?.let { audioSampleFormat = it }
    if(opts.bitRate > 0) {
      audioBitRate = opts.bitRate
    }
    opts.quality?.let { audioQuality = it }
    return getThis()
  }

  override fun useOptions(opts: VideoEncodingOptions): T {
    super.useOptions(opts)
    if(opts.bitRate > 0) {
      videoBitRate = opts.bitRate
    }
    opts.filter?.let { videoFilter = it }
    opts.preset?.let { videoPreset = it }
    return getThis()
  }

  override fun build(pass: Int): List<String> {
    checkNotNull(parent) {
      "Can not build without parent being set"
    }
    return build(parent, pass)
  }

  override fun build(parent: FFmpegBuilder, pass: Int): List<String> {
    if(pass > 0) {
      require(targetSize != 0L || videoBitRate != 0L) {
        "Target size, or video bitrate must be specified when using two-pass"
      }

      requireNotNull(format) { "Format must be specified when using two-pass" }
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
      val totalBitRate = (targetSize * 8 / durationInSeconds).toLong() - pass_padding_bitrate

      if(video_enabled && videoBitRate == 0L) {
        val audioBitRateForCalc = if(audio_enabled) audioBitRate else 0L
        videoBitRate = totalBitRate - audioBitRateForCalc
      } else if(audio_enabled && audioBitRate == 0L) {
        audioBitRate = totalBitRate
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

    check(videoBitRate <= 0L || videoQuality == null) { "Only one of video_bit_rate and video_quality can be set" }
    if(videoBitRate > 0L) {
      args.add("-b:v", videoBitRate.toString())
    }
    videoQuality?.let { args.add("-qscale:v", formatDecimalInteger(it)) }
    if(!Strings.isNullOrEmpty(videoPreset)) {
      args.add("-vpre", videoPreset)
    }
    if(!Strings.isNullOrEmpty(videoFilter)) {
      check(parent.inputs.size == 1) {
        "Video filter only works with one input, instead use setComplexVideoFilter(..)"
      }
      args.add("-vf", videoFilter)
    }
    if(!Strings.isNullOrEmpty(videoBitStreamFilter)) {
      args.add("-bsf:v", videoBitStreamFilter)
    }
    bFrames?.let { args.add("-bf", it.toString()) }
  }

  override fun addAudioFlags(args: ImmutableList.Builder<String>) {
    super.addAudioFlags(args)
    if(!Strings.isNullOrEmpty(audioSampleFormat)) {
      args.add("-sample_fmt", audioSampleFormat)
    }
    check(audioBitRate <= 0L || audioQuality == null || !throwWarnings) {
      "Only one of audio_bit_rate and audio_quality can be set"
    }
    if(audioBitRate > 0L) {
      args.add("-b:a", audioBitRate.toString())
    }
    audioQuality?.let { args.add("-qscale:a", formatDecimalInteger(it)) }
    if(!Strings.isNullOrEmpty(audioBitStreamFilter)) {
      args.add("-bsf:a", audioBitStreamFilter)
    }
    if(!Strings.isNullOrEmpty(audioFilter)) {
      args.add("-af", audioFilter)
    }
  }

  override fun addSourceTarget(pass: Int, args: ImmutableList.Builder<String>) {
    val filename = filename
    val uri = uri
    check(filename == null || uri == null) { "Only one of filename and uri can be set" }
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
