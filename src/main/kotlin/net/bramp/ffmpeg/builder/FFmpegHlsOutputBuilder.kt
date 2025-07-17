package net.bramp.ffmpeg.builder

import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.FFmpegUtils.toTimecode
import net.bramp.ffmpeg.Preconditions.checkNotEmpty
import java.util.concurrent.TimeUnit
import javax.annotation.CheckReturnValue

class FFmpegHlsOutputBuilder(parent: FFmpegBuilder, filename: String) :
  AbstractFFmpegOutputBuilder<FFmpegHlsOutputBuilder>(parent, filename) {
  var hlsTime: Long? = null
  var hlsSegmentFilename: String? = null
  var hlsInitTime: Long? = null
  var hlsListSize: Int? = null

  @JvmField
  var hlsBaseUrl: String? = null

  init {
    format = "hls"
  }

  override fun setFormat(format: String): FFmpegHlsOutputBuilder {
    if(format != "hls") {
      throw IllegalArgumentException(
        "Format cannot be set to anything else except 'hls' for FFmpegHlsOutputBuilder",
      )
    }
    super.setFormat(format)
    return this
  }

  /**
   * Set the target segment length. Default value is 2 seconds.
   *
   * @param duration hls_time to set
   * @param units The units the offset is in
   * @return [FFmpegHlsOutputBuilder]
   */
  fun setHlsTime(duration: Long, units: TimeUnit): FFmpegHlsOutputBuilder {
    hlsTime = units.toMillis(duration)
    return this
  }

  /**
   * hls_segment_filename Examples <br></br>
   * <br></br>
   * "file%03d.ts" segment files: file000.ts, file001.ts, file002.ts, etc.
   *
   * @param filename hls_segment_file_name to set
   * @return [FFmpegHlsOutputBuilder]
   */
  fun setHlsSegmentFileName(filename: String?): FFmpegHlsOutputBuilder {
    hlsSegmentFilename = checkNotEmpty(filename, "filename must not be empty")
    return this
  }

  /**
   * **Segment will be cut on the next key frame after this time has passed on the first m3u8
   * list.** <br></br>
   *
   * @param duration hls_init_time to set
   * @param units The units the offset is in
   * @return [FFmpegHlsOutputBuilder]
   */
  fun setHlsInitTime(duration: Long, units: TimeUnit): FFmpegHlsOutputBuilder {
    hlsInitTime = units.toMillis(duration)
    return this
  }

  /**
   * **Set the maximum number of playlist entries. If set to 0 the list file will contain all the
   * segments .** <br></br>
   * Default value is 5 <br></br>
   *
   * @param size hls_time to set
   * @return [FFmpegHlsOutputBuilder]
   */
  fun setHlsListSize(size: Int): FFmpegHlsOutputBuilder {
    com.google.common.base.Preconditions.checkArgument(size >= 0, "Size cannot be less than 0.")
    hlsListSize = size
    return this
  }

  /**
   * **Append baseurl to every entry in the playlist. Useful to generate playlists with absolute
   * paths. <br></br>
   * Note that the playlist sequence number must be unique for each segment and it is not to be
   * confused with the segment filename sequence number which can be cyclic, for example if the wrap
   * option is specified.** <br></br>
   *
   * @param baseurl hls_base_url to set
   * @return [FFmpegHlsOutputBuilder]
   */
  fun setHlsBaseUrl(baseurl: String?): FFmpegHlsOutputBuilder {
    hlsBaseUrl = checkNotEmpty(baseurl, "baseurl must not be empty")
    return this
  }

  override fun addFormatArgs(args: ImmutableList.Builder<String>) {
    super.addFormatArgs(args)
    if(hlsTime != null) {
      args.add("-hls_time", toTimecode(hlsTime!!, TimeUnit.MILLISECONDS))
    }
    if(!Strings.isNullOrEmpty(hlsSegmentFilename)) {
      args.add("-hls_segment_filename", hlsSegmentFilename)
    }
    if(hlsInitTime != null) {
      args.add("-hls_init_time", toTimecode(hlsInitTime!!, TimeUnit.MILLISECONDS))
    }
    if(hlsListSize != null) {
      args.add("-hls_list_size", hlsListSize.toString())
    }
    if(!Strings.isNullOrEmpty(hlsBaseUrl)) {
      args.add("-hls_base_url", hlsBaseUrl)
    }
  }

  @CheckReturnValue
  override fun getThis(): FFmpegHlsOutputBuilder = this
}
