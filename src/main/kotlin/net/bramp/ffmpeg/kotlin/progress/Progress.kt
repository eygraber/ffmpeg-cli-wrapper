package net.bramp.ffmpeg.kotlin.progress

import net.bramp.ffmpeg.kotlin.FFmpegUtils
import org.apache.commons.lang3.math.Fraction
import org.slf4j.LoggerFactory

data class Progress(
  /** The frame number being processed  */
  val frame: Long = 0,
  /** The current frames per second  */
  val fps: Fraction = Fraction.ZERO,
  /** Current bitrate  */
  val bitrate: Long = 0,
  /** Output file size (in bytes)  */
  val totalSize: Long = 0,
  // TODO Change this to a java.time.Duration
  /** Output time (in nanoseconds)  */
  val outTimeNs: Long = 0,
  val dupFrames: Long = 0,
  /** Number of frames dropped  */
  val dropFrames: Long = 0,
  /** Speed of transcoding. 1 means realtime, 2 means twice realtime.  */
  val speed: Float = 0f,
  /** Current status, can be one of "continue", or "end"  */
  val status: Status? = null,
) {
  enum class Status(private val status: String) {
    Continue("continue"),
    End("end"),
    ;

    override fun toString(): String = status

    companion object {
      /**
       * Returns the canonical status for this String or throws a IllegalArgumentException.
       *
       * @param status the status to convert to a Status enum.
       * @return the Status enum.
       * @throws IllegalArgumentException if the status is unknown.
       */
      fun of(status: String): Status =
        entries.firstOrNull { it.status.equals(status, ignoreCase = true) }
          ?: throw IllegalArgumentException("invalid progress status '$status'")
    }
  }

  val isEnd: Boolean
    get() = status == Status.End

  /**
   * Parses values from the line, into this object.
   *
   * The value options are defined in ffmpeg.c's print_report function
   * https://github.com/FFmpeg/FFmpeg/blob/master/ffmpeg.c
   *
   * @param line A single line of output from ffmpeg
   * @return true if the record is finished
   */
  @Suppress("ReturnCount")
  internal fun parseLine(line: String): Pair<Progress?, Boolean> {
    val trimmedLine = line.trim()
    if(trimmedLine.isEmpty()) {
      return null to false // Skip empty lines
    }
    val (key, value) = trimmedLine.split("=", limit = 2).takeIf { it.size == 2 } ?: return null to false

    return when(key) {
      "frame" -> copy(frame = value.toLong()) to false
      "fps" -> copy(fps = Fraction.getFraction(value)) to false
      "bitrate" -> copy(bitrate = value.takeUnless { it == "N/A" }?.let(FFmpegUtils::parseBitrate) ?: -1) to false
      "total_size" -> copy(totalSize = value.takeUnless { it == "N/A" }?.toLong() ?: -1) to false
      "out_time_ms", "out_time_us" -> null to false
      "out_time" -> copy(outTimeNs = FFmpegUtils.fromTimecode(value)) to false
      "dup_frames" -> copy(dupFrames = value.toLong()) to false
      "drop_frames" -> copy(dropFrames = value.toLong()) to false
      "speed" -> copy(speed = value.takeUnless { it == "N/A" }?.replace("x", "")?.toFloat() ?: -1f) to false
      "progress" -> copy(status = Status.of(value)) to true // The status field is always last in the record
      else -> {
        if(!key.startsWith("stream_")) {
          LOG.warn("skipping unhandled key: {} = {}", key, value)
        }
        // TODO handle stream_0_0_q=0.0:
        // stream_%d_%d_q= file_index, index, quality
        // stream_%d_%d_psnr_%c=%2.2f, file_index, index, type{Y, U, V}, quality // Enable with AV_CODEC_FLAG_PSNR
        // stream_%d_%d_psnr_all
        null to false
      }
    }
  }

  companion object {
    val LOG = LoggerFactory.getLogger(Progress::class.java)
  }
}
