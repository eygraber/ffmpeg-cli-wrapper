package net.bramp.ffmpeg

import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.FFmpegJob
import net.bramp.ffmpeg.job.SinglePassFFmpegJob
import net.bramp.ffmpeg.job.TwoPassFFmpegJob
import net.bramp.ffmpeg.progress.ProgressListener
import java.io.IOException

class FFmpegExecutor @JvmOverloads @Throws(IOException::class) constructor(
  val ffmpeg: FFmpeg = FFmpeg(),
  val ffprobe: FFprobe = FFprobe(FFprobe.DEFAULT_PATH),
) {

  fun createJob(builder: FFmpegBuilder): FFmpegJob = SinglePassFFmpegJob(ffmpeg, builder)

  fun createJob(builder: FFmpegBuilder, listener: ProgressListener): FFmpegJob = SinglePassFFmpegJob(
    ffmpeg,
    builder,
    listener,
  )

  /**
   * Creates a two pass job, which will execute FFmpeg twice to produce a better quality output.
   * More info: https://trac.ffmpeg.org/wiki/x264EncodingGuide#twopass
   *
   * @param builder The FFmpegBuilder
   * @return A new two-pass FFmpegJob
   */
  fun createTwoPassJob(builder: FFmpegBuilder): FFmpegJob = TwoPassFFmpegJob(ffmpeg, builder)

  fun createTwoPassJob(builder: FFmpegBuilder, listener: ProgressListener): FFmpegJob = TwoPassFFmpegJob(
    ffmpeg,
    builder,
    listener,
  )
}
