package net.bramp.ffmpeg.probe

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import java.io.Serializable

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegError(val code: Int = 0, val string: String = "") : Serializable {
  companion object {
    @Suppress("ObjectPropertyNaming")
    private const val serialVersionUID = 1L
  }
}
