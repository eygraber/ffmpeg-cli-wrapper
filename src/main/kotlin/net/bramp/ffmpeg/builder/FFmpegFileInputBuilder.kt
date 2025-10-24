package net.bramp.ffmpeg.builder

import net.bramp.ffmpeg.probe.FFmpegProbeResult

class FFmpegFileInputBuilder : AbstractFFmpegInputBuilder<FFmpegFileInputBuilder> {
  constructor(parent: FFmpegBuilder, filename: String) : super(parent, filename)
  constructor(parent: FFmpegBuilder, filename: String, result: FFmpegProbeResult) : super(
    parent,
    result,
    filename,
  )

  override fun getThis(): FFmpegFileInputBuilder = this

  override fun addSourceTarget(pass: Int, args: MutableList<String>) {
    val filename = filename
    val uri = uri

    check(!(filename != null && uri != null)) { "Only one of filename and uri can be set" }

    // Input
    if(filename != null) {
      args.add("-i")
      args.add(filename)
    }
    else if(uri != null) {
      args.add("-i")
      args.add(uri.toString())
    }
    else {
      assert(false)
    }
  }
}
