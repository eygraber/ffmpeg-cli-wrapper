package net.bramp.ffmpeg.builder

import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.Preconditions.checkNotEmpty
import javax.annotation.CheckReturnValue

/** Builds a ffprobe command line  */
class FFprobeBuilder {
  private var showFormat = true
  private var showStreams = true
  private var showChapters = true
  private var showFrames = false
  private var showPackets = false
  private var userAgent: String? = null
  private var input: String? = null
  private val extraArgs: MutableList<String> = ArrayList()

  fun setShowFormat(showFormat: Boolean): FFprobeBuilder {
    this.showFormat = showFormat
    return this
  }

  fun setShowStreams(showStreams: Boolean): FFprobeBuilder {
    this.showStreams = showStreams
    return this
  }

  fun setShowChapters(showChapters: Boolean): FFprobeBuilder {
    this.showChapters = showChapters
    return this
  }

  fun setShowFrames(showFrames: Boolean): FFprobeBuilder {
    this.showFrames = showFrames
    return this
  }

  fun setShowPackets(showPackets: Boolean): FFprobeBuilder {
    this.showPackets = showPackets
    return this
  }

  fun setUserAgent(userAgent: String?): FFprobeBuilder {
    this.userAgent = userAgent
    return this
  }

  fun setInput(filename: String?): FFprobeBuilder {
    input = filename!!
    return this
  }

  fun addExtraArgs(vararg values: String?): FFprobeBuilder {
    Preconditions.checkArgument(values.isNotEmpty(), "one or more values must be supplied")
    checkNotEmpty(values[0], "first extra arg may not be empty")
    for(value in values) {
      extraArgs.add(value!!)
    }
    return this
  }

  @CheckReturnValue
  fun build(): List<String> {
    val args = ImmutableList.builder<String>()
    Preconditions.checkNotNull(input, "Input must be specified")
    args.add("-v", "quiet").add("-print_format", "json").add("-show_error")
    if(userAgent != null) {
      args.add("-user_agent", userAgent)
    }
    args.addAll(extraArgs)
    if(showFormat) args.add("-show_format")
    if(showStreams) args.add("-show_streams")
    if(showChapters) args.add("-show_chapters")
    if(showPackets) args.add("-show_packets")
    if(showFrames) args.add("-show_frames")
    args.add(input)
    return args.build()
  }
}
