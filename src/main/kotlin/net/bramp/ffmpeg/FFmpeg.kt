package net.bramp.ffmpeg

import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.info.ChannelLayout
import net.bramp.ffmpeg.info.Codec
import net.bramp.ffmpeg.info.Filter
import net.bramp.ffmpeg.info.FilterPattern
import net.bramp.ffmpeg.info.Format
import net.bramp.ffmpeg.info.InfoParser.parseLayouts
import net.bramp.ffmpeg.info.PixelFormat
import net.bramp.ffmpeg.progress.ProgressListener
import net.bramp.ffmpeg.progress.ProgressParser
import net.bramp.ffmpeg.progress.TcpProgressParser
import org.apache.commons.lang3.math.Fraction
import java.io.IOException
import java.net.URISyntaxException
import java.util.Collections
import java.util.regex.Pattern
import javax.annotation.CheckReturnValue
import javax.annotation.Nonnull

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
  /** Supported codecs  */
  private var codecs: List<Codec>? = null

  /** Supported formats  */
  private var formats: List<Format>? = null

  /** Supported pixel formats  */
  private var pixelFormats: List<PixelFormat>? = null

  /** Supported filters  */
  private var filters: List<Filter>? = null

  /** Supported channel layouts  */
  private var channelLayouts: List<ChannelLayout>? = null

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

  @Nonnull
  @Synchronized
  @Throws(IOException::class)
  fun codecs(): List<Codec>? {
    checkIfFfmpeg()

    if(this.codecs == null) {
      val codecs = ArrayList<Codec>()

      val p = runFunc.run(ImmutableList.of(path, "-codecs"))
      try {
        wrapInReader(p).forEachLine { line ->
          val m = codecsRegex.matcher(line)
          if (m.matches()) {
            codecs.add(Codec(m.group(2), m.group(3), m.group(1)))
          }
        }

        throwOnError(p)
        this.codecs = ImmutableList.copyOf(codecs)
      } finally {
        p.destroy()
      }
    }

    return codecs
  }

  @Nonnull
  @Synchronized
  @Throws(IOException::class)
  fun filters(): List<Filter>? {
    checkIfFfmpeg()

    if(this.filters == null) {
      val filters = ArrayList<Filter>()

      val p = runFunc.run(ImmutableList.of(path, "-filters"))
      try {
        wrapInReader(p).forEachLine { line ->
          val m = filtersRegex.matcher(line)
          if (m.matches()) {
            // (?<inputpattern>[AVN|]+)->(?<outputpattern>[AVN|]+)\s+(?<description>.*)$
            filters.add(
              Filter(
                isTimelineSupported = m.group("timelinesupport") == "T",
                isSliceThreading = m.group("slicethreading") == "S",
                isCommandSupport = m.group("commandsupport") == "C",
                name = m.group("name"),
                inputPattern = FilterPattern(m.group("inputpattern")),
                outputPattern = FilterPattern(m.group("outputpattern")),
                description = m.group("description"),
              ),
            )
          }
        }

        throwOnError(p)
        this.filters = ImmutableList.copyOf(filters)
      } finally {
        p.destroy()
      }
    }

    return this.filters
  }

  @Nonnull
  @Synchronized
  @Throws(IOException::class)
  fun formats(): List<Format>? {
    checkIfFfmpeg()

    if(this.formats == null) {
      val formats = ArrayList<Format>()

      val p = runFunc.run(ImmutableList.of(path, "-formats"))
      try {
        wrapInReader(p).forEachLine { line ->
          val m = formatsRegex.matcher(line)
          if (m.matches()) {
            formats.add(Format(m.group(2), m.group(3), m.group(1)))
          }
        }

        throwOnError(p)
        this.formats = ImmutableList.copyOf(formats)
      } finally {
        p.destroy()
      }
    }
    return formats
  }

  @Synchronized
  @Throws(IOException::class)
  fun pixelFormats(): List<PixelFormat>? {
    checkIfFfmpeg()

    if(this.pixelFormats == null) {
      val pixelFormats = ArrayList<PixelFormat>()

      val p = runFunc.run(ImmutableList.of(path, "-pix_fmts"))
      try {
        wrapInReader(p).forEachLine { line ->
          val m = pixelFormatsRegex.matcher(line)
          if (m.matches()) {
            val flags = m.group(1)

            pixelFormats.add(
              PixelFormat(
                name = m.group(2),
                numberOfComponents = m.group(3).toInt(),
                bitsPerPixel = m.group(4).toInt(),
                flags = flags,
              ),
            )
          }
        }

        throwOnError(p)
        this.pixelFormats = ImmutableList.copyOf(pixelFormats)
      } finally {
        p.destroy()
      }
    }

    return pixelFormats
  }

  @Synchronized
  @Throws(IOException::class)
  fun channelLayouts(): List<ChannelLayout>? {
    checkIfFfmpeg()

    if(this.channelLayouts == null) {
      val p = runFunc.run(ImmutableList.of(path, "-layouts"))

      try {
        val r = wrapInReader(p)
        this.channelLayouts = Collections.unmodifiableList(parseLayouts(r))
      } finally {
        p.destroy()
      }
    }

    return channelLayouts
  }

  @Throws(IOException::class)
  fun createProgressParser(listener: ProgressListener): ProgressParser {
    // TODO In future create the best kind for this OS, unix socket, named pipe, or TCP.
    try {
      // Default to TCP because it is supported across all OSes, and is better than UDP because it
      // provides good properties such as in-order packets, reliability, error checking, etc.
      return TcpProgressParser(listener)
    }
    catch(e: URISyntaxException) {
      throw IOException(e)
    }
  }

  @Throws(IOException::class)
  fun runWithBuilder(builder: FFmpegBuilder) {
    runWithBuilder(builder, null)
  }

  @Throws(IOException::class)
  fun runWithBuilder(builder: FFmpegBuilder, listener: ProgressListener?) {
    if(listener != null) {
      createProgressParser(listener).use { progressParser ->
        progressParser.start()
        // Ensure builder is treated as a new instance if addProgress modifies it and returns a new one
        val finalBuilder = builder.addProgress(progressParser.uri)
        super.run(finalBuilder.build())
      }
    }
    else {
      super.run(builder.build())
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
    if(listener == null) {
      run(builder.build())
    }
    else {
      createProgressParser(listener).use { progressParser ->
        progressParser.start()
        val builderWithProgress = builder.addProgress(progressParser.uri)
        run(builderWithProgress.build())
      }
    }
  }

  @CheckReturnValue
  fun builder(): FFmpegBuilder = FFmpegBuilder()

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

    val codecsRegex: Pattern =
      Pattern.compile("^ ([.D][.E][VASD][.I][.L][.S]) (\\S{2,})\\s+(.*)$")
    val formatsRegex: Pattern = Pattern.compile("^ ([ D][ E]) (\\S+)\\s+(.*)$")
    val pixelFormatsRegex: Pattern =
      Pattern.compile("^([.I][.O][.H][.P][.B]) (\\S{2,})\\s+(\\d+)\\s+(\\d+)$")
    val filtersRegex: Pattern = Pattern.compile(
      "^\\s*(?<timelinesupport>[T.])(?<slicethreading>[S.])(?<commandsupport>[C.])\\s(?<name>[A-Za-z0-9_]+)\\s+(?<inputpattern>[AVN|]+)->(?<outputpattern>[AVN|]+)\\s+(?<description>.*)$",
    )
  }
}
