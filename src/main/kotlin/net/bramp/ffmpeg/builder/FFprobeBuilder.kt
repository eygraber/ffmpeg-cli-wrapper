package net.bramp.ffmpeg.builder

import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.Preconditions.checkNotNullEmptyOrBlank
import javax.annotation.CheckReturnValue

/** Builds a ffprobe command line  */
class FFprobeBuilder {
  private var isShowFormat = true
  private var isShowStreams = true
  private var isShowChapters = true
  private var isShowFrames = false
  private var isShowPackets = false
  private var userAgent: String? = null
  private var input: String? = null
  private val extraArgs: MutableList<String> = ArrayList()

  fun setShowFormat(showFormat: Boolean): FFprobeBuilder {
    this.isShowFormat = showFormat
    return this
  }

  fun setShowStreams(showStreams: Boolean): FFprobeBuilder {
    this.isShowStreams = showStreams
    return this
  }

  fun setShowChapters(showChapters: Boolean): FFprobeBuilder {
    this.isShowChapters = showChapters
    return this
  }

  fun setShowFrames(showFrames: Boolean): FFprobeBuilder {
    this.isShowFrames = showFrames
    return this
  }

  fun setShowPackets(showPackets: Boolean): FFprobeBuilder {
    this.isShowPackets = showPackets
    return this
  }

  fun setUserAgent(userAgent: String?): FFprobeBuilder {
    this.userAgent = userAgent
    return this
  }

  fun setInput(filename: String): FFprobeBuilder {
    input = filename
    return this
  }

  fun addExtraArgs(vararg values: String): FFprobeBuilder {
    require(values.isNotEmpty()) {
      "one or more values must be supplied"
    }
    checkNotNullEmptyOrBlank(values[0], "first extra arg may not be empty")
    for(value in values) {
      extraArgs.add(value)
    }
    return this
  }

  @CheckReturnValue
  fun build(): List<String> {
    val args = ImmutableList.builder<String>()
    val input = input
    checkNotNull(input) {
      "Input must be specified"
    }

    args.add("-v", "quiet").add("-print_format", "json").add("-show_error")
    userAgent?.let {
      args.add("-user_agent", it)
    }
    args.addAll(extraArgs)
    if(isShowFormat) args.add("-show_format")
    if(isShowStreams) args.add("-show_streams")
    if(isShowChapters) args.add("-show_chapters")
    if(isShowPackets) args.add("-show_packets")
    if(isShowFrames) args.add("-show_frames")
    args.add(input)
    return args.build()
  }
}
