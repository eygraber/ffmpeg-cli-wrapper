package net.bramp.ffmpeg.builder

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.modelmapper.Mapper
import net.bramp.ffmpeg.options.AudioEncodingOptions
import net.bramp.ffmpeg.options.EncodingOptions
import net.bramp.ffmpeg.options.MainEncodingOptions
import net.bramp.ffmpeg.options.VideoEncodingOptions
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.lang3.math.Fraction
import java.net.URI
import java.util.concurrent.TimeUnit

@Suppress("VariableNaming", "BooleanPropertyNaming")
abstract class AbstractFFmpegStreamBuilder<T : AbstractFFmpegStreamBuilder<T>> {
  val parent: FFmpegBuilder?

  @JvmField
  var filename: String? = null
  var uri: URI? = null

  @JvmField
  var format: String? = null

  var startOffset: Long? = null // in milliseconds
  var duration: Long? = null // in milliseconds

  val meta_tags: MutableList<String> = ArrayList()

  var audio_enabled: Boolean = true
  var audio_codec: String? = null
  var audio_channels: Int = 0
  var audio_sample_rate: Int = 0
  var audio_preset: String? = null

  var video_enabled: Boolean = true
  var video_codec: String? = null
  var video_copyinkf: Boolean = false
  var video_frame_rate: Fraction? = null
  var video_width: Int = 0
  var video_height: Int = 0
  var video_size: String? = null
  var video_movflags: String? = null
  var video_frames: Int? = null
  var video_pixel_format: String? = null

  var subtitle_enabled: Boolean = true
  var subtitle_preset: String? = null
  var subtitle_codec: String? = null // Was private, made public var for simplicity, or use setter

  @JvmField
  var preset: String? = null

  @JvmField
  var presetFilename: String? = null

  val extra_args: MutableList<String> = ArrayList()

  @JvmField
  var strict: Strict = Strict.Normal

  @JvmField
  var targetSize: Long = 0L
  var pass_padding_bitrate: Long = 1024L

  var throwWarnings: Boolean = true

  protected constructor() {
    this.parent = null
  }

  protected constructor(parent: FFmpegBuilder, filename: String) {
    this.parent = Preconditions.checkNotNull(parent)
    this.filename = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(filename, "filename must not be empty")
  }

  protected constructor(parent: FFmpegBuilder, uri: URI) {
    this.parent = Preconditions.checkNotNull(parent)
    this.uri = net.bramp.ffmpeg.Preconditions.checkValidStream(uri)
  }

  protected abstract fun getThis(): T

  fun useOptions(opts: EncodingOptions): T {
    Mapper.map(opts, this)
    return getThis()
  }

  fun useOptions(opts: MainEncodingOptions): T {
    Mapper.map(opts, this)
    return getThis()
  }

  fun useOptions(opts: AudioEncodingOptions): T {
    Mapper.map(opts, this)
    return getThis()
  }

  fun useOptions(opts: VideoEncodingOptions): T {
    Mapper.map(opts, this)
    return getThis()
  }

  fun disableVideo(): T {
    this.video_enabled = false
    return getThis()
  }

  fun disableAudio(): T {
    this.audio_enabled = false
    return getThis()
  }

  fun disableSubtitle(): T {
    this.subtitle_enabled = false
    return getThis()
  }

  fun setPresetFilename(presetFilename: String): T {
    this.presetFilename =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(presetFilename, "file preset must not be empty")
    return getThis()
  }

  fun setPreset(preset: String): T {
    this.preset = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(preset, "preset must not be empty")
    return getThis()
  }

  fun setFilename(filename: String): T {
    this.filename = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(filename, "filename must not be empty")
    return getThis()
  }

  fun setUri(uri: URI): T {
    this.uri = net.bramp.ffmpeg.Preconditions.checkValidStream(uri)
    return getThis()
  }

  open fun setFormat(format: String): T {
    this.format = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(format, "format must not be empty")
    return getThis()
  }

  fun setVideoCodec(codec: String): T {
    this.video_enabled = true
    this.video_codec = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(codec, "codec must not be empty")
    return getThis()
  }

  fun setVideoCopyInkf(copyinkf: Boolean): T {
    this.video_enabled = true
    this.video_copyinkf = copyinkf
    return getThis()
  }

  fun setVideoMovFlags(movflags: String): T {
    this.video_enabled = true
    this.video_movflags =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(movflags, "movflags must not be empty")
    return getThis()
  }

  fun setVideoFrameRate(frameRate: Fraction): T {
    this.video_enabled = true
    this.video_frame_rate = Preconditions.checkNotNull(frameRate)
    return getThis()
  }

  fun setVideoFrameRate(frames: Int, per: Int): T = setVideoFrameRate(Fraction.getFraction(frames, per))

  fun setVideoFrameRate(frameRate: Double): T = setVideoFrameRate(Fraction.getFraction(frameRate))

  fun setFrames(frames: Int): T {
    this.video_enabled = true
    this.video_frames = frames
    return getThis()
  }

  fun setVideoWidth(width: Int): T {
    require(isValidSize(width)) { "Width must be -1 or greater than zero" }
    this.video_enabled = true
    this.video_width = width
    return getThis()
  }

  fun setVideoHeight(height: Int): T {
    require(isValidSize(height)) { "Height must be -1 or greater than zero" }
    this.video_enabled = true
    this.video_height = height
    return getThis()
  }

  fun setVideoResolution(width: Int, height: Int): T {
    require(isValidSize(width) && isValidSize(height)) {
      "Both width and height must be -1 or greater than zero"
    }
    this.video_enabled = true
    this.video_width = width
    this.video_height = height
    return getThis()
  }

  fun setVideoResolution(abbreviation: String): T {
    this.video_enabled = true
    this.video_size =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(abbreviation, "video abbreviation must not be empty")
    return getThis()
  }

  fun setVideoPixelFormat(format: String): T {
    this.video_enabled = true
    this.video_pixel_format =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(format, "format must not be empty")
    return getThis()
  }

  fun addMetaTag(key: String, value: String): T {
    MetadataSpecifier.checkValidKey(key)
    net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(value, "value must not be empty")
    meta_tags.add("-metadata")
    meta_tags.add("$key=$value")
    return getThis()
  }

  fun addMetaTag(spec: MetadataSpecifier, key: String, value: String): T {
    MetadataSpecifier.checkValidKey(key)
    net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(value, "value must not be empty")
    meta_tags.add("-metadata:${spec.spec}")
    meta_tags.add("$key=$value")
    return getThis()
  }

  fun setAudioCodec(codec: String): T {
    this.audio_enabled = true
    this.audio_codec = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(codec, "codec must not be empty")
    return getThis()
  }

  fun setSubtitleCodec(codec: String): T {
    this.subtitle_enabled = true
    this.subtitle_codec = net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(codec, "codec must not be empty")
    return getThis()
  }

  fun setAudioChannels(channels: Int): T {
    require(channels > 0) { "channels must be positive" }
    this.audio_enabled = true
    this.audio_channels = channels
    return getThis()
  }

  fun setAudioSampleRate(sampleRate: Int): T {
    require(sampleRate > 0) { "sample rate must be positive" }
    this.audio_enabled = true
    this.audio_sample_rate = sampleRate
    return getThis()
  }

  fun setTargetSize(targetSize: Long): T {
    require(targetSize > 0) { "target size must be positive" }
    this.targetSize = targetSize
    return getThis()
  }

  fun setStartOffset(offset: Long, units: TimeUnit): T {
    this.startOffset = units.toMillis(offset)
    return getThis()
  }

  fun setDuration(duration: Long, units: TimeUnit): T {
    this.duration = units.toMillis(duration)
    return getThis()
  }

  fun setStrict(strict: Strict): T {
    this.strict = Preconditions.checkNotNull(strict)
    return getThis()
  }

  fun setPassPaddingBitrate(bitrate: Long): T {
    require(bitrate > 0) { "bitrate must be positive" }
    this.pass_padding_bitrate = bitrate
    return getThis()
  }

  fun setAudioPreset(preset: String): T {
    this.audio_enabled = true
    this.audio_preset =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(preset, "audio preset must not be empty")
    return getThis()
  }

  fun setSubtitlePreset(preset: String): T {
    this.subtitle_enabled = true
    this.subtitle_preset =
      net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(preset, "subtitle preset must not be empty")
    return getThis()
  }

  fun addExtraArgs(vararg values: String): T {
    require(values.isNotEmpty()) { "one or more values must be supplied" }
    net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank(values[0], "first extra arg may not be empty")

    for(value in values) {
      extra_args.add(Preconditions.checkNotNull(value))
    }
    return getThis()
  }

  fun done(): FFmpegBuilder {
    checkNotNull(parent) { "Can not call done without parent being set" }
    return parent
  }

  abstract fun buildOptions(): EncodingOptions?

  open fun build(pass: Int): List<String> {
    checkNotNull(parent) { "Can not build without parent being set" }
    return build(parent, pass)
  }

  internal open fun build(parent: FFmpegBuilder, pass: Int): List<String> {
    val args = ImmutableList.builder<String>()
    addGlobalFlags(parent, args)

    if(video_enabled) {
      addVideoFlags(parent, args)
    }
    else {
      args.add("-vn")
    }

    if(audio_enabled && pass != 1) {
      addAudioFlags(args)
    }
    else {
      args.add("-an")
    }

    if(subtitle_enabled) {
      if(!Strings.isNullOrEmpty(subtitle_codec)) {
        args.add("-scodec", subtitle_codec)
      }
      if(!Strings.isNullOrEmpty(subtitle_preset)) {
        args.add("-spre", subtitle_preset)
      }
    }
    else {
      args.add("-sn")
    }

    addFormatArgs(args)
    args.addAll(extra_args)
    addSourceTarget(pass, args)

    return args.build()
  }

  protected abstract fun addSourceTarget(pass: Int, args: ImmutableList.Builder<String>)

  protected open fun addGlobalFlags(parent: FFmpegBuilder, args: ImmutableList.Builder<String>) {
    if(strict != Strict.Normal) {
      args.add("-strict", strict.toString().lowercase())
    }
    if(!Strings.isNullOrEmpty(format)) {
      args.add("-f", format)
    }
    if(!Strings.isNullOrEmpty(preset)) {
      args.add("-preset", preset)
    }
    if(!Strings.isNullOrEmpty(presetFilename)) {
      args.add("-fpre", presetFilename)
    }
    startOffset?.let { args.add("-ss", FFmpegUtils.toTimecode(it, TimeUnit.MILLISECONDS)) }
    duration?.let { args.add("-t", FFmpegUtils.toTimecode(it, TimeUnit.MILLISECONDS)) }
    args.addAll(meta_tags)
  }

  protected open fun addAudioFlags(args: ImmutableList.Builder<String>) {
    if(!Strings.isNullOrEmpty(audio_codec)) {
      args.add("-acodec", audio_codec)
    }
    if(audio_channels > 0) {
      args.add("-ac", audio_channels.toString())
    }
    if(audio_sample_rate > 0) {
      args.add("-ar", audio_sample_rate.toString())
    }
    if(!Strings.isNullOrEmpty(audio_preset)) {
      args.add("-apre", audio_preset)
    }
  }

  protected open fun addVideoFlags(parent: FFmpegBuilder, args: ImmutableList.Builder<String>) {
    video_frames?.let { args.add("-vframes", it.toString()) }
    if(!Strings.isNullOrEmpty(video_codec)) {
      args.add("-vcodec", video_codec)
    }
    if(!Strings.isNullOrEmpty(video_pixel_format)) {
      args.add("-pix_fmt", video_pixel_format)
    }
    if(video_copyinkf) {
      args.add("-copyinkf")
    }
    if(!Strings.isNullOrEmpty(video_movflags)) {
      args.add("-movflags", video_movflags)
    }

    val videoSize = video_size
    if(videoSize != null) {
      require(video_width == 0 && video_height == 0) {
        "Can not specific width or height, as well as an abbreviatied video size"
      }
      args.add("-s", videoSize)
    }
    else if(video_width != 0 && video_height != 0) {
      args.add("-s", "${video_width}x${video_height}")
    }
    video_frame_rate?.let { args.add("-r", it.toString()) }
  }

  protected open fun addFormatArgs(args: ImmutableList.Builder<String>) {}

  companion object {
    @JvmField
    internal val DEVNULL: String = if(SystemUtils.IS_OS_WINDOWS) "NUL" else "/dev/null"

    @JvmStatic
    internal fun isValidSize(widthOrHeight: Int): Boolean = widthOrHeight > 0 || widthOrHeight == -1
  }
}
