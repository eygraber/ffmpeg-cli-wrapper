package net.bramp.ffmpeg.progress

import com.google.common.base.MoreObjects
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.FFmpegUtils.fromTimecode
import org.apache.commons.lang3.math.Fraction
import org.slf4j.LoggerFactory
import java.util.Objects
import javax.annotation.CheckReturnValue

// TODO Change to be immutable
data class Progress(
  /** The frame number being processed  */
  var frame: Long = 0,
  /** The current frames per second  */
  var fps: Fraction = Fraction.ZERO,
  /** Current bitrate  */
  var bitrate: Long = 0,
  /** Output file size (in bytes)  */
  var total_size: Long = 0,
  // TODO Change this to a java.time.Duration
  /** Output time (in nanoseconds)  */
  var out_time_ns: Long = 0,
  var dup_frames: Long = 0,
  /** Number of frames dropped  */
  var drop_frames: Long = 0,
  /** Speed of transcoding. 1 means realtime, 2 means twice realtime.  */
  var speed: Float = 0f,
  /** Current status, can be one of "continue", or "end"  */
  var status: Status? = null,
) {
  enum class Status(private val status: String) {
    CONTINUE("continue"),
    END("end"),
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
      fun of(status: String): Status {
        for(s in entries) {
          if (status.equals(s.status, ignoreCase = true)) {
            return s
          }
        }
        throw IllegalArgumentException("invalid progress status '$status'")
      }
    }
  }

  /**
   * Parses values from the line, into this object.
   *
   * The value options are defined in ffmpeg.c's print_report function
   * https://github.com/FFmpeg/FFmpeg/blob/master/ffmpeg.c
   *
   * @param line A single line of output from ffmpeg
   * @return true if the record is finished
   */
  internal fun parseLine(line: String): Boolean {
    val trimmedLine = line.trim()
    if(trimmedLine.isEmpty()) {
      return false // Skip empty lines
    }
    val args = trimmedLine.split("=".toRegex(), 2).toTypedArray()
    if(args.size != 2) {
      // invalid argument, so skip
      return false
    }
    val key = args[0]
    val value = args[1]
    when(key) {
      "frame" -> {
        frame = value.toLong()
        return false
      }

      "fps" -> {
        fps = Fraction.getFraction(value)
        return false
      }

      "bitrate" -> {
        bitrate = if (value == "N/A") {
          -1
        }
        else {
          FFmpegUtils.parseBitrate(value)
        }
        return false
      }

      "total_size" -> {
        total_size = if (value == "N/A") {
          -1
        }
        else {
          value.toLong()
        }
        return false
      }

      "out_time_ms" -> // This is a duplicate of the "out_time" field, but expressed as a int instead of string.
        // Note this value is in microseconds, not milliseconds, and is based on AV_TIME_BASE which
        // could change.
        // out_time_ns = Long.parseLong(value) * 1000;
        return false

      "out_time_us" -> return false
      "out_time" -> {
        out_time_ns = fromTimecode(value)
        return false
      }

      "dup_frames" -> {
        dup_frames = value.toLong()
        return false
      }

      "drop_frames" -> {
        drop_frames = value.toLong()
        return false
      }

      "speed" -> {
        speed = if (value == "N/A") {
          -1f
        }
        else {
          value.replace("x", "").toFloat()
        }
        return false
      }

      "progress" -> {
        // TODO After "end" stream is closed
        status = Status.of(value)
        return true // The status field is always last in the record
      }

      else -> {
        if (key.startsWith("stream_")) {
          // TODO handle stream_0_0_q=0.0:
          // stream_%d_%d_q= file_index, index, quality
          // stream_%d_%d_psnr_%c=%2.2f, file_index, index, type{Y, U, V}, quality // Enable with
          // AV_CODEC_FLAG_PSNR
          // stream_%d_%d_psnr_all
        }
        else {
          LOG.warn("skipping unhandled key: {} = {}", key, value)
        }
        return false // Either way, not supported
      }
    }
  }

  @get:CheckReturnValue
  val isEnd: Boolean
    get() = status == Status.END

  override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other !is Progress) return false
    val progress1 = other
    return frame == progress1.frame && bitrate == progress1.bitrate && total_size == progress1.total_size &&
      out_time_ns == progress1.out_time_ns &&
      dup_frames == progress1.dup_frames &&
      drop_frames == progress1.drop_frames &&
      progress1.speed.compareTo(
        speed,
      ) == 0 && fps == progress1.fps && status == progress1.status
  }

  override fun hashCode(): Int = Objects.hash(
    frame,
    fps,
    bitrate,
    total_size,
    out_time_ns,
    dup_frames,
    drop_frames,
    speed,
    status,
  )

  override fun toString(): String = MoreObjects.toStringHelper(this)
    .add("frame", frame)
    .add("fps", fps)
    .add("bitrate", bitrate)
    .add("total_size", total_size)
    .add("out_time_ns", out_time_ns)
    .add("dup_frames", dup_frames)
    .add("drop_frames", drop_frames)
    .add("speed", speed)
    .add("status", status)
    .toString()

  companion object {
    val LOG = LoggerFactory.getLogger(Progress::class.java)
  }
}
