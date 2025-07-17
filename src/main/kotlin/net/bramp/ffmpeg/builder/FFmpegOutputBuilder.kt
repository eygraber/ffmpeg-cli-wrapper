package net.bramp.ffmpeg.builder

import java.net.URI

class FFmpegOutputBuilder : AbstractFFmpegOutputBuilder<FFmpegOutputBuilder> {
  constructor() : super()
  internal constructor(parent: FFmpegBuilder, filename: String) : super(parent, filename)
  internal constructor(parent: FFmpegBuilder, uri: URI) : super(parent, uri)

  override fun getThis(): FFmpegOutputBuilder = this
}
