package net.bramp.ffmpeg

import net.bramp.ffmpeg.probe.FFmpegError
import java.io.IOException

class FFmpegException(message: String?, val error: FFmpegError?) : IOException(message)
