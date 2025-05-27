package net.bramp.ffmpeg.builder

import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.probe.FFmpegProbeResult

class FFmpegFileInputBuilder : AbstractFFmpegInputBuilder<FFmpegFileInputBuilder> {

  // Constructor matching public FFmpegFileInputBuilder(FFmpegBuilder parent, String filename)
  constructor(parent: FFmpegBuilder, filename: String) : super(parent, filename)

  // Constructor matching public FFmpegFileInputBuilder(FFmpegBuilder parent, String filename, FFmpegProbeResult result)
  // Note: In the abstract class, probeResult is nullable.
  constructor(
      parent: FFmpegBuilder,
      filename: String, 
      result: FFmpegProbeResult? 
  ) : super(parent, result, filename)


  override fun addSourceTarget(pass: Int, args: ImmutableList.Builder<String>) {
    // filename and uri are inherited properties from AbstractFFmpegStreamBuilder
    if (filename != null && uri != null) {
      throw IllegalStateException("Only one of filename and uri can be set for an input.")
    }

    when {
      filename != null -> args.add("-i", filename)
      uri != null -> args.add("-i", uri.toString())
      else -> {
        // This case should ideally not be reached if constructors ensure filename or uri is set.
        // If it can be reached, it indicates an invalid state.
        throw IllegalStateException("Input must have either a filename or a URI.")
      }
    }
  }

  // getThis() is inherited from AbstractFFmpegStreamBuilder and should correctly return FFmpegFileInputBuilder
  // No explicit override needed here if the superclass's getThis() is:
  // @Suppress("UNCHECKED_CAST")
  // protected open fun getThis(): T = this as T
}
