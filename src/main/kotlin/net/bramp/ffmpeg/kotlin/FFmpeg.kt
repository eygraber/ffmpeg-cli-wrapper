package net.bramp.ffmpeg.kotlin

import net.bramp.ffmpeg.kotlin.builder.FFmpegBuilder
import net.bramp.ffmpeg.kotlin.info.ChannelLayout
import net.bramp.ffmpeg.kotlin.info.Codec
import net.bramp.ffmpeg.kotlin.info.Filter
import net.bramp.ffmpeg.kotlin.info.FilterPattern
import net.bramp.ffmpeg.kotlin.info.Format
import net.bramp.ffmpeg.kotlin.info.InfoParser
import net.bramp.ffmpeg.kotlin.info.PixelFormat
import net.bramp.ffmpeg.kotlin.progress.ProgressListener
import net.bramp.ffmpeg.kotlin.progress.ProgressParser
import net.bramp.ffmpeg.kotlin.progress.TcpProgressParser
import org.apache.commons.lang3.math.Fraction
import java.io.IOException
import java.net.URISyntaxException
import kotlin.text.get

/**
 * Wrapper around FFmpeg
 *
 * @author bramp
 */
@Suppress("ObjectPropertyNaming")
class FFmpeg @JvmOverloads constructor(
  path: String = DEFAULT_PATH,
  runFunction: ProcessFunction = RunProcessFunction(),
) : FFcommon(path, runFunction) {
  init {
    version()
  }

  /**
   * Returns true if the binary we are using is the true ffmpeg. This is to avoid conflict with
   * avconv (from the libav project), that some symlink to ffmpeg.
   *
   * @return true iff this is the official ffmpeg binary.
   * @throws IOException If a I/O error occurs while executing ffmpeg.
   */
  @get:Throws(IOException::class)
  val isFfmpeg: Boolean
    get() = version().startsWith("ffmpeg")

  /**
   * Throws an exception if this is an unsupported version of ffmpeg.
   *
   * @throws IllegalArgumentException if this is not the official ffmpeg binary.
   * @throws IOException If a I/O error occurs while executing ffmpeg.
   */
  @Throws(IllegalArgumentException::class, IOException::class)
  private fun checkIfFfmpeg() {
    require(this.isFfmpeg) { "This binary '$path' is not a supported version of ffmpeg" }
  }

  @Throws(IOException::class)
  fun codecs(): List<Codec> = codecs

  @Throws(IOException::class)
  fun filters(): List<Filter> = filters

  @Throws(IOException::class)
  fun formats(): List<Format> = formats

  @Throws(IOException::class)
  fun pixelFormats(): List<PixelFormat> = pixelFormats

  @Throws(IOException::class)
  fun channelLayouts(): List<ChannelLayout> = channelLayouts

  @Throws(IOException::class)
  fun createProgressParser(listener: ProgressListener): ProgressParser =
    // TODO In future create the best kind for this OS, unix socket, named pipe, or TCP.
    // Default to TCP because it is supported across all OSes, and is better than UDP because it
    // provides good properties such as in-order packets, reliability, error checking, etc.
    try {
      TcpProgressParser(listener)
    }
    catch(e: URISyntaxException) {
      throw IOException(e)
    }

  @Throws(IOException::class)
  fun runWithBuilder(builder: FFmpegBuilder, listener: ProgressListener? = null) {
    when(listener) {
      null -> super.run(builder.build())
      else -> createProgressParser(listener).use { progressParser ->
        progressParser.start()
        val finalBuilder = builder.addProgress(progressParser.uri)
        super.run(finalBuilder.build())
      }
    }
  }

  @Throws(IOException::class)
  override fun run(args: List<String>) {
    checkIfFfmpeg()
    super.run(args)
  }

  @JvmOverloads
  @Throws(IOException::class)
  fun run(builder: FFmpegBuilder, listener: ProgressListener? = null) {
    when(listener) {
      null -> run(builder.build())
      else -> createProgressParser(listener).use { progressParser ->
        progressParser.start()
        val builderWithProgress = builder.addProgress(progressParser.uri)
        run(builderWithProgress.build())
      }
    }
  }

  fun builder(): FFmpegBuilder = FFmpegBuilder()

  /** Supported codecs  */
  @Suppress("ClassOrdering") // These lazy properties are intentionally placed near their accessor methods
  private val codecs: List<Codec> by lazy {
    checkIfFfmpeg()
    val process = runFunc.run(listOf(path, "-codecs"))
    try {
      wrapInReader(process).useLines { lines ->
        lines.mapNotNull { line ->
          codecsRegex.matchEntire(line)?.let { match ->
            Codec(match.groupValues[2], match.groupValues[3], match.groupValues[1])
          }
        }.toList()
      }.also {
        throwOnError(process)
      }
    }
    finally {
      process.destroy()
    }
  }

  /** Supported filters  */
  @Suppress("ClassOrdering")
  private val filters: List<Filter> by lazy {
    checkIfFfmpeg()
    val process = runFunc.run(listOf(path, "-filters"))
    try {
      wrapInReader(process).useLines { lines ->
        lines.mapNotNull { line ->
          filtersRegex.matchEntire(line)?.let { match ->
            Filter(
              isTimelineSupported = match.groups["timelinesupport"]?.value == "T",
              isSliceThreading = match.groups["slicethreading"]?.value == "S",
              isCommandSupport = match.groups["commandsupport"]?.value == "C",
              name = match.groups["name"]?.value ?: "",
              inputPattern = FilterPattern(match.groups["inputpattern"]?.value ?: ""),
              outputPattern = FilterPattern(match.groups["outputpattern"]?.value ?: ""),
              description = match.groups["description"]?.value ?: "",
            )
          }
        }.toList()
      }.also {
        throwOnError(process)
      }
    }
    finally {
      process.destroy()
    }
  }

  /** Supported formats  */
  @Suppress("ClassOrdering")
  private val formats: List<Format> by lazy {
    checkIfFfmpeg()
    val process = runFunc.run(listOf(path, "-formats"))
    try {
      wrapInReader(process).useLines { lines ->
        lines.mapNotNull { line ->
          formatsRegex.matchEntire(line)?.let { match ->
            Format(match.groupValues[2], match.groupValues[3], match.groupValues[1])
          }
        }.toList()
      }.also {
        throwOnError(process)
      }
    }
    finally {
      process.destroy()
    }
  }

  /** Supported pixel formats  */
  @Suppress("ClassOrdering")
  private val pixelFormats: List<PixelFormat> by lazy {
    checkIfFfmpeg()
    val process = runFunc.run(listOf(path, "-pix_fmts"))
    try {
      wrapInReader(process).useLines { lines ->
        lines.mapNotNull { line ->
          pixelFormatsRegex.matchEntire(line)?.let { match ->
            PixelFormat(
              name = match.groupValues[2],
              numberOfComponents = match.groupValues[3].toInt(),
              bitsPerPixel = match.groupValues[4].toInt(),
              flags = match.groupValues[1],
            )
          }
        }.toList()
      }.also {
        throwOnError(process)
      }
    }
    finally {
      process.destroy()
    }
  }

  /** Supported channel layouts  */
  @Suppress("ClassOrdering")
  private val channelLayouts: List<ChannelLayout> by lazy {
    checkIfFfmpeg()
    val process = runFunc.run(listOf(path, "-layouts"))
    try {
      InfoParser.parseLayouts(wrapInReader(process))
    }
    finally {
      process.destroy()
    }
  }

  companion object {
    const val FFMPEG_COMMAND: String = "ffmpeg"

    @JvmField
    val DEFAULT_PATH: String = System.getenv("FFMPEG") ?: FFMPEG_COMMAND

    @JvmField
    val FPS_30: Fraction = Fraction.getFraction(30, 1)

    @JvmField
    val FPS_29_97: Fraction = Fraction.getFraction(30_000, 1_001)

    @JvmField
    val FPS_24: Fraction = Fraction.getFraction(24, 1)

    @JvmField
    val FPS_23_976: Fraction = Fraction.getFraction(24_000, 1_001)

    const val AUDIO_MONO: Int = 1
    const val AUDIO_STEREO: Int = 2

    const val AUDIO_FORMAT_U8: String = "u8" // 8
    const val AUDIO_FORMAT_S16: String = "s16" // 16
    const val AUDIO_FORMAT_S32: String = "s32" // 32
    const val AUDIO_FORMAT_FLT: String = "flt" // 32
    const val AUDIO_FORMAT_DBL: String = "dbl" // 64

    @Deprecated("")
    val AUDIO_DEPTH_U8: String = AUDIO_FORMAT_U8

    @Deprecated("")
    val AUDIO_DEPTH_S16: String = AUDIO_FORMAT_S16

    @Deprecated("")
    val AUDIO_DEPTH_S32: String = AUDIO_FORMAT_S32

    @Deprecated("")
    val AUDIO_DEPTH_FLT: String = AUDIO_FORMAT_FLT

    @Deprecated("")
    val AUDIO_DEPTH_DBL: String = AUDIO_FORMAT_DBL

    const val AUDIO_SAMPLE_8000: Int = 8_000
    const val AUDIO_SAMPLE_11025: Int = 11_025
    const val AUDIO_SAMPLE_12000: Int = 12_000
    const val AUDIO_SAMPLE_16000: Int = 16_000
    const val AUDIO_SAMPLE_22050: Int = 22_050
    const val AUDIO_SAMPLE_32000: Int = 32_000
    const val AUDIO_SAMPLE_44100: Int = 44_100
    const val AUDIO_SAMPLE_48000: Int = 48_000
    const val AUDIO_SAMPLE_96000: Int = 96_000

    private val codecsRegex = Regex("^ ([.D][.E][VASD][.I][.L][.S]) (\\S{2,})\\s+(.*)$")
    private val formatsRegex = Regex("^ ([ D][ E]) (\\S+)\\s+(.*)$")
    private val pixelFormatsRegex = Regex("^([.I][.O][.H][.P][.B]) (\\S{2,})\\s+(\\d+)\\s+(\\d+)$")
    private val filtersRegex = Regex(
      "^\\s*(?<timelinesupport>[T.])(?<slicethreading>[S.])(?<commandsupport>[C.])\\s(?<name>[A-Za-z0-9_]+)\\s+(?<inputpattern>[AVN|]+)->(?<outputpattern>[AVN|]+)\\s+(?<description>.*)$",
    )
  }
}
