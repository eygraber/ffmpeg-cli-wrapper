package net.bramp.ffmpeg.progress

import com.google.common.base.MoreObjects
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.FFmpegUtils.fromTimecode
import org.apache.commons.lang3.math.Fraction
import org.slf4j.LoggerFactory
import java.util.Objects
import javax.annotation.CheckReturnValue

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
      fun of(status: String): Status {
        for(s in entries) {
          if(status.equals(s.status, ignoreCase = true)) {
            return s
          }
        }
        throw IllegalArgumentException("invalid progress status '$status'")
      }
    }
  }

  @get:CheckReturnValue
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
    val args = trimmedLine.split("=".toRegex(), 2).toTypedArray()
    if(args.size != 2) {
      // invalid argument, so skip
      return null to false
    }
    val key = args[0]
    val value = args[1]
    return when(key) {
      "frame" -> copy(frame = value.toLong()) to false
      "fps" -> copy(fps = Fraction.getFraction(value)) to false
      "bitrate" -> copy(
        bitrate =
        if(value == "N/A") {
          -1
        }
        else {
          FFmpegUtils.parseBitrate(value)
        },
      ) to false

      "total_size" -> copy(
        totalSize =
        if(value == "N/A") {
          -1
        }
        else {
          value.toLong()
        },
      ) to false

      // This is a duplicate of the "out_time" field, but expressed as a int instead of string.
      // Note this value is in microseconds, not milliseconds, and is based on AV_TIME_BASE which
      // could change.
      // out_time_ns = Long.parseLong(value) * 1000;
      "out_time_ms" -> null to false

      "out_time_us" -> null to false

      "out_time" -> copy(outTimeNs = fromTimecode(value)) to false
      "dup_frames" -> copy(dupFrames = value.toLong()) to false
      "drop_frames" -> copy(dropFrames = value.toLong()) to false
      "speed" -> copy(
        speed =
        if(value == "N/A") {
          -1f
        }
        else {
          value.replace("x", "").toFloat()
        },
      ) to false

      "progress" ->
        // TODO After "end" stream is closed
        copy(status = Status.of(value)) to true // The status field is always last in the record

      else -> {
        if(key.startsWith("stream_")) {
          // TODO handle stream_0_0_q=0.0:
          // stream_%d_%d_q= file_index, index, quality
          // stream_%d_%d_psnr_%c=%2.2f, file_index, index, type{Y, U, V}, quality // Enable with
          // AV_CODEC_FLAG_PSNR
          // stream_%d_%d_psnr_all
        }
        else {
          LOG.warn("skipping unhandled key: {} = {}", key, value)
        }
        null to false // Either way, not supported
      }
    }
  }

  override fun equals(other: Any?): Boolean {
    if(this === other) {
      return true
    }
    if(other !is Progress) {
      return false
    }
    val progress1 = other
    return frame == progress1.frame &&
      bitrate == progress1.bitrate &&
      totalSize == progress1.totalSize &&
      outTimeNs == progress1.outTimeNs &&
      dupFrames == progress1.dupFrames &&
      dropFrames == progress1.dropFrames &&
      progress1.speed.compareTo(speed) == 0 &&
      fps == progress1.fps &&
      status == progress1.status
  }

  override fun hashCode(): Int = Objects.hash(
    frame,
    fps,
    bitrate,
    totalSize,
    outTimeNs,
    dupFrames,
    dropFrames,
    speed,
    status,
  )

  override fun toString(): String = MoreObjects.toStringHelper(this)
    .add("frame", frame)
    .add("fps", fps)
    .add("bitrate", bitrate)
    .add("total_size", totalSize)
    .add("out_time_ns", outTimeNs)
    .add("dup_frames", dupFrames)
    .add("drop_frames", dropFrames)
    .add("speed", speed)
    .add("status", status)
    .toString()

  companion object {
    val LOG = LoggerFactory.getLogger(Progress::class.java)
  }
}
