package net.bramp.ffmpeg.kotlin.probe

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.bramp.ffmpeg.kotlin.FFmpegUtils
import java.util.ArrayList

@Serializable
data class FFmpegProbe(
  val error: FFmpegError? = null,
  val format: FFmpegFormat? = null,
  val streams: List<FFmpegStream> = ArrayList(),
  val chapters: List<FFmpegChapter> = ArrayList(),
) {
  companion object {
    @JvmStatic
    fun fromJson(json: String): FFmpegProbe = FFmpegUtils.json.decodeFromString(serializer<FFmpegProbe>(), json)
  }
}
