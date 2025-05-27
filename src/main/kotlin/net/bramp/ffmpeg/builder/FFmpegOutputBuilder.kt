package net.bramp.ffmpeg.builder

import java.net.URI

class FFmpegOutputBuilder : AbstractFFmpegOutputBuilder<FFmpegOutputBuilder> {

  // Public constructor, equivalent to Java's public no-arg constructor
  constructor() : super()

  // internal constructors to be used by FFmpegBuilder within the same module
  internal constructor(parent: FFmpegBuilder, filename: String) : super(parent, filename)

  internal constructor(parent: FFmpegBuilder, uri: URI) : super(parent, uri)

  // getThis() is inherited from AbstractFFmpegStreamBuilder and should correctly return FFmpegOutputBuilder
  // No explicit override needed here if the superclass's getThis() is:
  // @Suppress("UNCHECKED_CAST")
  // protected open fun getThis(): T = this as T
}
