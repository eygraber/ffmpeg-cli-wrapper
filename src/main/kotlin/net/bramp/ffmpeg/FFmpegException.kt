package net.bramp.ffmpeg

import java.io.IOException
import net.bramp.ffmpeg.probe.FFmpegError

class FFmpegException(message: String?, val error: FFmpegError?) : IOException(message) {
  companion object {
    private const val serialVersionUID = 3048288225568984942L
  }
}
