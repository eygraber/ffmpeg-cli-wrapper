package net.bramp.ffmpeg.kotlin.job

import net.bramp.ffmpeg.kotlin.FFmpeg
import net.bramp.ffmpeg.kotlin.progress.ProgressListener

/**
 * A FFmpegJob is a single job that can be run by FFmpeg. It can be a single pass, or a two pass
 * job.
 *
 * @author bramp
 */
abstract class FFmpegJob(val ffmpeg: FFmpeg, val listener: ProgressListener?) : Runnable {
  enum class State {
    Waiting,
    Running,
    Finished,
    Failed,
  }

  var state: State = State.Waiting
}
