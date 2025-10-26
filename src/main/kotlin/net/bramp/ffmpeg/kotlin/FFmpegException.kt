package net.bramp.ffmpeg.kotlin

import net.bramp.ffmpeg.kotlin.probe.FFmpegError
import java.io.IOException

class FFmpegException(message: String?, val error: FFmpegError?) : IOException(message)
