package net.bramp.ffmpeg

import java.io.IOException
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.FFmpegJob
import net.bramp.ffmpeg.job.SinglePassFFmpegJob
import net.bramp.ffmpeg.job.TwoPassFFmpegJob
import net.bramp.ffmpeg.progress.ProgressListener

class FFmpegExecutor @JvmOverloads @Throws(IOException::class) constructor(
    val ffmpeg: FFmpeg = FFmpeg(),
    val ffprobe: FFprobe = FFprobe()
) {

  // Secondary constructor to match public FFmpegExecutor(FFmpeg ffmpeg)
  // This is useful if Java code specifically calls this constructor.
  @Throws(IOException::class)
  constructor(ffmpeg: FFmpeg) : this(ffmpeg, FFprobe())

  fun createJob(builder: FFmpegBuilder): FFmpegJob {
    return SinglePassFFmpegJob(ffmpeg, builder)
  }

  fun createJob(builder: FFmpegBuilder, listener: ProgressListener): FFmpegJob {
    return SinglePassFFmpegJob(ffmpeg, builder, listener)
  }

  /**
   * Creates a two pass job, which will execute FFmpeg twice to produce a better quality output.
   * More info: https://trac.ffmpeg.org/wiki/x264EncodingGuide#twopass
   *
   * @param builder The FFmpegBuilder
   * @return A new two-pass FFmpegJob
   */
  fun createTwoPassJob(builder: FFmpegBuilder): FFmpegJob {
    return TwoPassFFmpegJob(ffmpeg, builder)
  }

  fun createTwoPassJob(builder: FFmpegBuilder, listener: ProgressListener): FFmpegJob {
    return TwoPassFFmpegJob(ffmpeg, builder, listener)
  }
}
