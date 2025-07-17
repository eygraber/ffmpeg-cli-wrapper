package net.bramp.ffmpeg

import com.google.common.base.MoreObjects
import com.google.common.base.Preconditions
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
class FFmpeg @JvmOverloads constructor(
  @Nonnull path: String = DEFAULT_PATH,
  @Nonnull runFunction: ProcessFunction = RunProcessFunction(),
) : FFcommon(path, runFunction) {

  /** Supported codecs  */
  var codecs: MutableList<Codec?>? = null

  /** Supported formats  */
  var formats: MutableList<Format?>? = null

  /** Supported pixel formats  */
  private var pixelFormats: MutableList<PixelFormat?>? = null

  /** Supported filters  */
  private var filters: MutableList<Filter?>? = null

  /** Supported channel layouts  */
  private var channelLayouts: MutableList<ChannelLayout?>? = null

  constructor(@Nonnull runFunction: ProcessFunction) : this(DEFAULT_PATH, runFunction)

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
  val isFFmpeg: Boolean
    get() = version().startsWith("ffmpeg")

  /**
   * Throws an exception if this is an unsupported version of ffmpeg.
   *
   * @throws IllegalArgumentException if this is not the official ffmpeg binary.
   * @throws IOException If a I/O error occurs while executing ffmpeg.
   */
  @Throws(IllegalArgumentException::class, IOException::class)
  private fun checkIfFFmpeg() {
    require(this.isFFmpeg) { "This binary '" + path + "' is not a supported version of ffmpeg" }
  }

  @Nonnull
  @Synchronized
  @Throws(IOException::class)
  fun codecs(): MutableList<Codec?> {
    checkIfFFmpeg()

    if(this.codecs == null) {
      codecs = ArrayList<Codec?>()

      val p = runFunc.run(ImmutableList.of<String>(path, "-codecs"))
      try {
        val r = wrapInReader(p)
        var line: String?
        while ((r.readLine().also { line = it }) != null) {
          val m = CODECS_REGEX.matcher(line)
          if (!m.matches()) continue

          codecs!!.add(Codec(m.group(2), m.group(3), m.group(1)))
        }

        throwOnError(p)
        this.codecs = ImmutableList.copyOf<Codec?>(codecs)
      } finally {
        p.destroy()
      }
    }

    return codecs!!
  }

  @Nonnull
  @Synchronized
  @Throws(IOException::class)
  fun filters(): MutableList<Filter?> {
    checkIfFFmpeg()

    if(this.filters == null) {
      filters = ArrayList<Filter?>()

      val p = runFunc.run(ImmutableList.of<String>(path, "-filters"))
      try {
        val r = wrapInReader(p)
        var line: String?
        while ((r.readLine().also { line = it }) != null) {
          val m = FILTERS_REGEX.matcher(line)
          if (!m.matches()) continue

          // (?<inputpattern>[AVN|]+)->(?<outputpattern>[AVN|]+)\s+(?<description>.*)$
          filters!!.add(
            Filter(
              m.group("timelinesupport") == "T",
              m.group("slicethreading") == "S",
              m.group("commandsupport") == "C",
              m.group("name"),
              FilterPattern(m.group("inputpattern")),
              FilterPattern(m.group("outputpattern")),
              m.group("description"),
            ),
          )
        }

        throwOnError(p)
        this.filters = ImmutableList.copyOf<Filter?>(filters)
      } finally {
        p.destroy()
      }
    }

    return this.filters!!
  }

  @Nonnull
  @Synchronized
  @Throws(IOException::class)
  fun formats(): MutableList<Format?> {
    checkIfFFmpeg()

    if(this.formats == null) {
      formats = ArrayList<Format?>()

      val p = runFunc.run(ImmutableList.of<String>(path, "-formats"))
      try {
        val r = wrapInReader(p)
        var line: String?
        while ((r.readLine().also { line = it }) != null) {
          val m = FORMATS_REGEX.matcher(line)
          if (!m.matches()) continue

          formats!!.add(Format(m.group(2), m.group(3), m.group(1)))
        }

        throwOnError(p)
        this.formats = ImmutableList.copyOf<Format?>(formats)
      } finally {
        p.destroy()
      }
    }
    return formats!!
  }

  @Synchronized
  @Throws(IOException::class)
  fun pixelFormats(): MutableList<PixelFormat?>? {
    checkIfFFmpeg()

    if(this.pixelFormats == null) {
      pixelFormats = ArrayList<PixelFormat?>()

      val p = runFunc.run(ImmutableList.of<String>(path, "-pix_fmts"))
      try {
        val r = wrapInReader(p)
        var line: String?
        while ((r.readLine().also { line = it }) != null) {
          val m = PIXEL_FORMATS_REGEX.matcher(line)
          if (!m.matches()) continue
          val flags = m.group(1)

          pixelFormats!!.add(
            PixelFormat(
              m.group(2),
              m.group(3).toInt(),
              m.group(4).toInt(),
              flags,
            ),
          )
        }

        throwOnError(p)
        this.pixelFormats = ImmutableList.copyOf<PixelFormat?>(pixelFormats)
      } finally {
        p.destroy()
      }
    }

    return pixelFormats
  }

  @Synchronized
  @Throws(IOException::class)
  fun channelLayouts(): MutableList<ChannelLayout?>? {
    checkIfFFmpeg()

    if(this.channelLayouts == null) {
      val p = runFunc.run(ImmutableList.of<String>(path, "-layouts"))

      try {
        val r = wrapInReader(p)
        this.channelLayouts = Collections.unmodifiableList<ChannelLayout?>(parseLayouts(r))
      } finally {
        p.destroy()
      }
    }

    return this.channelLayouts
  }

  @Throws(IOException::class)
  protected fun createProgressParser(listener: ProgressListener?): ProgressParser {
    // TODO In future create the best kind for this OS, unix socket, named pipe, or TCP.
    try {
      // Default to TCP because it is supported across all OSes, and is better than UDP because it
      // provides good properties such as in-order packets, reliability, error checking, etc.
      return TcpProgressParser(Preconditions.checkNotNull<ProgressListener?>(listener))
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
    checkNotNull(builder)
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
    checkIfFFmpeg()
    super.run(args)
  }

  @JvmOverloads
  @Throws(IOException::class)
  fun run(builder: FFmpegBuilder?, listener: ProgressListener? = null) {
    var builder = builder
    Preconditions.checkNotNull<FFmpegBuilder?>(builder)

    if(listener != null) {
      createProgressParser(listener).use { progressParser ->
        progressParser.start()
        builder = builder!!.addProgress(progressParser.uri)
        run(builder.build())
      }
    }
    else {
      run(builder!!.build())
    }
  }

  @CheckReturnValue
  fun builder(): FFmpegBuilder = FFmpegBuilder()

  companion object {
    const val FFMPEG: String = "ffmpeg"

    @JvmField
    val DEFAULT_PATH: String =
      MoreObjects.firstNonNull<String?>(System.getenv("FFMPEG"), FFMPEG)

    @JvmField
    val FPS_30: Fraction = Fraction.getFraction(30, 1)
    @JvmField
    val FPS_29_97: Fraction = Fraction.getFraction(30000, 1001)
    @JvmField
    val FPS_24: Fraction = Fraction.getFraction(24, 1)
    @JvmField
    val FPS_23_976: Fraction = Fraction.getFraction(24000, 1001)

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

    const val AUDIO_SAMPLE_8000: Int = 8000
    const val AUDIO_SAMPLE_11025: Int = 11025
    const val AUDIO_SAMPLE_12000: Int = 12000
    const val AUDIO_SAMPLE_16000: Int = 16000
    const val AUDIO_SAMPLE_22050: Int = 22050
    const val AUDIO_SAMPLE_32000: Int = 32000
    const val AUDIO_SAMPLE_44100: Int = 44100
    const val AUDIO_SAMPLE_48000: Int = 48000
    const val AUDIO_SAMPLE_96000: Int = 96000

    val CODECS_REGEX: Pattern =
      Pattern.compile("^ ([.D][.E][VASD][.I][.L][.S]) (\\S{2,})\\s+(.*)$")
    val FORMATS_REGEX: Pattern = Pattern.compile("^ ([ D][ E]) (\\S+)\\s+(.*)$")
    val PIXEL_FORMATS_REGEX: Pattern =
      Pattern.compile("^([.I][.O][.H][.P][.B]) (\\S{2,})\\s+(\\d+)\\s+(\\d+)$")
    val FILTERS_REGEX: Pattern = Pattern.compile(
      "^\\s*(?<timelinesupport>[T.])(?<slicethreading>[S.])(?<commandsupport>[C.])\\s(?<name>[A-Za-z0-9_]+)\\s+(?<inputpattern>[AVN|]+)->(?<outputpattern>[AVN|]+)\\s+(?<description>.*)$",
    )
  }
}
