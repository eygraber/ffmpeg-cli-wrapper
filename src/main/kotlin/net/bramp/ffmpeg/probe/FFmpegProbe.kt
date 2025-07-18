package net.bramp.ffmpeg.probe

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.FFmpegUtils
import java.util.ArrayList

@Serializable
data class FFmpegProbe(
  var error: FFmpegError? = null,
  var format: FFmpegFormat? = null,
  var streams: List<FFmpegStream> = ArrayList(),
  var chapters: List<FFmpegChapter> = ArrayList(),
) {
  companion object {
    @JvmStatic
    fun fromJson(json: String): FFmpegProbe {
      return FFmpegUtils.json.decodeFromString(serializer<FFmpegProbe>(), json)
    }
  }
}
