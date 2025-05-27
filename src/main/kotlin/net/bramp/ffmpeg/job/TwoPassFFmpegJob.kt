package net.bramp.ffmpeg.job

import com.google.common.base.Throwables
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.progress.ProgressListener
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

class TwoPassFFmpegJob(
  ffmpeg: FFmpeg,
  val builder: FFmpegBuilder,
  listener: ProgressListener? = null,
) : FFmpegJob(ffmpeg, listener) {
  private val passlogPrefix: String

  init {
    // Random prefix so multiple runs don't clash
    passlogPrefix = UUID.randomUUID().toString()
    builder.setPassPrefix(passlogPrefix)

    // Build the args now (but throw away the results). This allows the illegal arguments to be
    // caught early, but also allows the ffmpeg command to actually alter the arguments when
    // running.
    builder.setPass(1).build()
  }

  @Throws(IOException::class)
  private fun deletePassLog() {
    val cwd = Paths.get("")
    Files.newDirectoryStream(cwd, "$passlogPrefix*.log*").use { stream ->
      for(p in stream) {
        Files.deleteIfExists(p)
      }
    }
  }

  override fun run() {
    state = State.Running
    try {
      try {
        // Two pass
        val shouldOverride = builder.overrideOutputFiles
        val b1 = builder.setPass(1).overrideOutputFiles(true)
        ffmpeg.runWithBuilder(b1, listener)
        val b2 = builder.setPass(2).overrideOutputFiles(shouldOverride)
        ffmpeg.runWithBuilder(b2, listener)
      } finally {
        deletePassLog()
      }
      state = State.Finished
    }
    catch(t: Throwable) {
      state = State.Failed
      Throwables.throwIfUnchecked(t)
      throw RuntimeException(t)
    }
  }
}
