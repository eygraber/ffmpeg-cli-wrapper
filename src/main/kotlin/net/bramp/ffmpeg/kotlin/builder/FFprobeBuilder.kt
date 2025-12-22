package net.bramp.ffmpeg.kotlin.builder

import net.bramp.ffmpeg.kotlin.Preconditions

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
    Preconditions.checkNotNullEmptyOrBlank(arg = values[0], errorMessage = "first extra arg may not be empty")
    for(value in values) {
      extraArgs.add(value)
    }
    return this
  }

  fun build(): List<String> {
    val input = input
    checkNotNull(input) {
      "Input must be specified"
    }

    return buildList {
      add("-v")
      add("quiet")
      add("-print_format")
      add("json")
      add("-show_error")
      userAgent?.let { agent ->
        add("-user_agent")
        add(agent)
      }
      addAll(extraArgs)
      if(isShowFormat) add("-show_format")
      if(isShowStreams) add("-show_streams")
      if(isShowChapters) add("-show_chapters")
      if(isShowPackets) add("-show_packets")
      if(isShowFrames) add("-show_frames")
      add(input)
    }
  }
}
