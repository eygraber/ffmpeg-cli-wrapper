package net.bramp.ffmpeg

import com.google.common.base.CharMatcher
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.bramp.ffmpeg.serde.FFmpegFrameOrPacketSerializer
import net.bramp.ffmpeg.serde.FFmpegStreamSideDataSerializer
import net.bramp.ffmpeg.serde.FractionSerializer
import net.bramp.ffmpeg.serde.LowercaseEnumSerializer
import net.bramp.ffmpeg.shared.CodecType
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

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

  internal val bitrateRegex: Pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)kbits/s")
  internal val timeRegex: Pattern = Pattern.compile("(\\d+):(\\d+):(\\d+(?:\\.\\d+)?)")
  internal val zeroMatcher: CharMatcher = CharMatcher.`is`('0')

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
      "net.bramp.ffmpeg.FFmpegUtils",
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
      String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)
    }
    else {
      zeroMatcher.trimTrailingFrom(
        String.format(Locale.ROOT, "%02d:%02d:%02d.%09d", hours, minutes, seconds, ns),
      )
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

    val m = timeRegex.matcher(time)
    require(m.find()) { "invalid time '$time'" }

    val hours = m.group(1).toLong()
    val mins = m.group(2).toLong()
    val secs = m.group(3).toDouble()

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
    val m = bitrateRegex.matcher(bitrate)
    require(m.find()) { "Invalid bitrate '$bitrate'" }

    return (m.group(1).toFloat() * 1000).toLong()
  }
}
