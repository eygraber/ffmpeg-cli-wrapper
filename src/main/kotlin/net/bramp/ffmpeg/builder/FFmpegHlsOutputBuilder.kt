package net.bramp.ffmpeg.builder

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import java.util.concurrent.TimeUnit
import net.bramp.ffmpeg.FFmpegUtils // Assuming FFmpegUtils.kt exists

class FFmpegHlsOutputBuilder : AbstractFFmpegOutputBuilder<FFmpegHlsOutputBuilder> {

  var hls_time: Long? = null
  var hls_segment_filename: String? = null
  var hls_init_time: Long? = null
  var hls_list_size: Int? = null
  var hls_base_url: String? = null

  internal constructor(parent: FFmpegBuilder, filename: String) : super(parent, filename) {
    this.format = "hls" // Default format for HLS
  }

  override fun setFormat(format: String?): FFmpegHlsOutputBuilder {
    // Allow 'hls' or null (if someone tries to reset it, though super might disallow null)
    // but enforce it's 'hls' if not null.
    if (format != null && format != "hls") {
      throw IllegalArgumentException(
          "Format cannot be set to anything else except 'hls' for FFmpegHlsOutputBuilder. Attempted: $format")
    }
    // If format is null, let superclass decide (it might default or throw).
    // If format is "hls", then it's fine.
    super.setFormat(format ?: "hls") // Pass "hls" if null, otherwise the format (which must be "hls")
    return this
  }

  fun setHlsTime(duration: Long, units: TimeUnit): FFmpegHlsOutputBuilder {
    Preconditions.checkNotNull(units)
    this.hls_time = units.toMillis(duration)
    return this
  }

  fun setHlsSegmentFileName(filename: String): FFmpegHlsOutputBuilder {
    this.hls_segment_filename = net.bramp.ffmpeg.Preconditions.checkNotEmpty(filename, "filename must not be empty")
    return this
  }

  fun setHlsInitTime(duration: Long, units: TimeUnit): FFmpegHlsOutputBuilder {
    Preconditions.checkNotNull(units)
    this.hls_init_time = units.toMillis(duration)
    return this
  }

  fun setHlsListSize(size: Int): FFmpegHlsOutputBuilder {
    require(size >= 0) { "Size cannot be less than 0." }
    this.hls_list_size = size
    return this
  }

  fun setHlsBaseUrl(baseurl: String): FFmpegHlsOutputBuilder {
    this.hls_base_url = net.bramp.ffmpeg.Preconditions.checkNotEmpty(baseurl, "baseurl must not be empty")
    return this
  }

  override fun addFormatArgs(args: ImmutableList.Builder<String>) {
    super.addFormatArgs(args) // Add args from AbstractFFmpegOutputBuilder first

    hls_time?.let { args.add("-hls_time", FFmpegUtils.toTimecode(it, TimeUnit.MILLISECONDS)) }
    
    if (!Strings.isNullOrEmpty(hls_segment_filename)) {
      args.add("-hls_segment_filename", hls_segment_filename)
    }

    hls_init_time?.let { args.add("-hls_init_time", FFmpegUtils.toTimecode(it, TimeUnit.MILLISECONDS)) }
    
    hls_list_size?.let { args.add("-hls_list_size", it.toString()) }
    
    if (!Strings.isNullOrEmpty(hls_base_url)) {
      args.add("-hls_base_url", hls_base_url)
    }
  }

  // getThis() is inherited from AbstractFFmpegStreamBuilder
  // For a concrete class like this, it will correctly return FFmpegHlsOutputBuilder
  // due to the generic T being resolved.
  // No override for getThis() is needed here.
}
