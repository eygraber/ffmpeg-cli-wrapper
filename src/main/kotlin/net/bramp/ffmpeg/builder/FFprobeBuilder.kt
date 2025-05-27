package net.bramp.ffmpeg.builder

import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableList
import java.util.ArrayList
import net.bramp.ffmpeg.Preconditions as FfmpegProjectPreconditions

class FFprobeBuilder {
  private var showFormat: Boolean = true
  private var showStreams: Boolean = true
  private var showChapters: Boolean = true
  private var showFrames: Boolean = false
  private var showPackets: Boolean = false
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

  fun setInput(filename: String): FFprobeBuilder {
    this.input = Preconditions.checkNotNull(filename)
    return this
  }

  fun addExtraArgs(vararg values: String): FFprobeBuilder {
    require(values.isNotEmpty()) { "one or more values must be supplied" }
    FfmpegProjectPreconditions.checkNotEmpty(values[0], "first extra arg may not be empty")

    for (value in values) {
      extraArgs.add(Preconditions.checkNotNull(value))
    }
    return this
  }

  fun build(): List<String> {
    val args = ImmutableList.builder<String>()

    Preconditions.checkNotNull(input, "Input must be specified")

    args.add("-v", "quiet") // Verbosity quiet
        .add("-print_format", "json")
        .add("-show_error") // Always show errors

    userAgent?.let { args.add("-user_agent", it) }

    args.addAll(extraArgs)

    if (showFormat) args.add("-show_format")
    if (showStreams) args.add("-show_streams")
    if (showChapters) args.add("-show_chapters")
    if (showPackets) args.add("-show_packets")
    if (showFrames) args.add("-show_frames")

    // Input is guaranteed non-null here by Preconditions.checkNotNull above
    args.add(input!!)

    return args.build()
  }
}
