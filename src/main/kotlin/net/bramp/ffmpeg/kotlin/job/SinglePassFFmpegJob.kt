package net.bramp.ffmpeg.kotlin.job

import net.bramp.ffmpeg.kotlin.FFmpeg
import net.bramp.ffmpeg.kotlin.builder.FFmpegBuilder
import net.bramp.ffmpeg.kotlin.progress.ProgressListener

class SinglePassFFmpegJob(
  ffmpeg: FFmpeg,
  val builder: FFmpegBuilder,
  listener: ProgressListener? = null,
) : FFmpegJob(ffmpeg, listener) {

  init {
    // Build the args now (but throw away the results). This allows the illegal arguments to be
    // caught early, but also allows the ffmpeg command to actually alter the arguments when
    // running.
    builder.build()
  }

  override fun run() {
    state = State.Running
    runCatching {
      ffmpeg.runWithBuilder(builder, listener)
    }
      .onSuccess {
        state = State.Finished
      }
      .onFailure { t ->
        state = State.Failed
        throw t
      }
  }
}
