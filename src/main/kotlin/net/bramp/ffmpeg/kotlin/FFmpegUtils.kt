package net.bramp.ffmpeg.kotlin

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.bramp.ffmpeg.kotlin.serialization.FFmpegFrameOrPacketSerializer
import net.bramp.ffmpeg.kotlin.serialization.FFmpegStreamSideDataSerializer
import net.bramp.ffmpeg.kotlin.serialization.FractionSerializer
import net.bramp.ffmpeg.kotlin.serialization.LowercaseEnumSerializer
import net.bramp.ffmpeg.kotlin.shared.CodecType
import java.util.concurrent.TimeUnit

/** Helper class with commonly used methods */
object FFmpegUtils {

  val json: Json = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
      contextual(FractionSerializer)
      contextual(FFmpegFrameOrPacketSerializer)
      contextual(FFmpegStreamSideDataSerializer)
      contextual(LowercaseEnumSerializer<CodecType>())
    }
  }

  internal val bitrateRegex: Regex = Regex("(\\d+(?:\\.\\d+)?)kbits/s")
  internal val timeRegex: Regex = Regex("(\\d+):(\\d+):(\\d+(?:\\.\\d+)?)")

  /**
   * Convert milliseconds to "hh:mm:ss.ms" String representation.
   *
   * @param milliseconds time duration in milliseconds
   * @return time duration in human-readable format
   * @deprecated please use #toTimecode() instead.
   */
  @Deprecated(
    "please use #toTimecode() instead.",
    ReplaceWith(
      "FFmpegUtils.toTimecode(milliseconds, TimeUnit.MILLISECONDS)",
      "java.util.concurrent.TimeUnit", // Assuming MILLISECONDS is imported via TimeUnit
      "net.bramp.ffmpeg.kotlin.FFmpegUtils",
    ),
  )
  @JvmStatic
  fun millisecondsToString(milliseconds: Long): String = toTimecode(milliseconds, TimeUnit.MILLISECONDS)

  /**
   * Convert the duration to "hh:mm:ss" timecode representation, where ss (seconds) can be decimal.
   *
   * @param duration the duration.
   * @param units the unit the duration is in.
   * @return the timecode representation.
   */
  @JvmStatic
  fun toTimecode(duration: Long, units: TimeUnit): String {
    // TODO Negative durations are also supported.
    // https://www.ffmpeg.org/ffmpeg-utils.html#Time-duration
    require(duration >= 0) { "duration must be positive" }

    val nanoseconds = units.toNanos(duration)
    var seconds = units.toSeconds(duration)
    val ns = nanoseconds - TimeUnit.SECONDS.toNanos(seconds)

    var minutes = TimeUnit.SECONDS.toMinutes(seconds)
    seconds -= TimeUnit.MINUTES.toSeconds(minutes)

    val hours = TimeUnit.MINUTES.toHours(minutes)
    minutes -= TimeUnit.HOURS.toMinutes(hours)

    return if(ns == 0L) {
      "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
    else {
      val nanoStr = "%09d".format(ns).trimEnd('0').trimEnd('.')
      "%02d:%02d:%02d.%s".format(hours, minutes, seconds, nanoStr)
    }
  }

  /**
   * Returns the number of nanoseconds this timecode represents. The string is expected to be in the
   * format "hour:minute:second", where second can be a decimal number.
   *
   * @param time the timecode to parse.
   * @return the number of nanoseconds or -1 if time is 'N/A'
   */
  @JvmStatic
  fun fromTimecode(time: String): Long {
    Preconditions.checkNotNullEmptyOrBlank(time, "time must not be empty string")

    if(time == "N/A") {
      return -1L
    }

    val m = timeRegex.find(time)
    require(m != null) { "invalid time '$time'" }

    val hours = m.groups[1]!!.value.toLong()
    val mins = m.groups[2]!!.value.toLong()
    val secs = m.groups[3]!!.value.toDouble()

    return TimeUnit.HOURS.toNanos(hours) +
      TimeUnit.MINUTES.toNanos(mins) +
      (TimeUnit.SECONDS.toNanos(1) * secs).toLong()
  }

  /**
   * Converts a string representation of bitrate to a long of bits per second
   *
   * @param bitrate in the form of 12.3kbits/s
   * @return the bitrate in bits per second or -1 if bitrate is 'N/A'
   */
  @JvmStatic
  fun parseBitrate(bitrate: String): Long {
    Preconditions.checkNotNullEmptyOrBlank(bitrate, "bitrate must not be empty string")

    if(bitrate == "N/A") {
      return -1L
    }
    val m = bitrateRegex.find(bitrate)
    require(m != null) { "Invalid bitrate '$bitrate'" }

    return (m.groups[1]!!.value.toFloat() * 1000).toLong()
  }
}
