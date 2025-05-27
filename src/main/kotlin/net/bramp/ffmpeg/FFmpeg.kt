package net.bramp.ffmpeg

import com.google.common.base.MoreObjects
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.ImmutableList
import java.io.BufferedReader
import java.io.IOException
import java.net.URISyntaxException
import java.util.regex.Pattern
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.info.ChannelLayout
import net.bramp.ffmpeg.info.Codec
import net.bramp.ffmpeg.info.Filter
import net.bramp.ffmpeg.info.FilterPattern
import net.bramp.ffmpeg.info.Format
import net.bramp.ffmpeg.info.InfoParser
import net.bramp.ffmpeg.info.PixelFormat
import net.bramp.ffmpeg.progress.ProgressListener
import net.bramp.ffmpeg.progress.ProgressParser
import net.bramp.ffmpeg.progress.TcpProgressParser
import org.apache.commons.lang3.math.Fraction

/**
 * Wrapper around FFmpeg
 *
 * @author bramp
 */
class FFmpeg : FFcommon {

  @Throws(IOException::class)
  constructor() : this(DEFAULT_PATH, RunProcessFunction())

  @Throws(IOException::class)
  constructor(runFunction: ProcessFunction) : this(DEFAULT_PATH, runFunction)

  @Throws(IOException::class)
  constructor(path: String) : this(path, RunProcessFunction())

  @Throws(IOException::class)
  constructor(path: String, runFunction: ProcessFunction) : super(path, runFunction) {
    version() // Call version in constructor to check binary is working
  }

  /** Supported codecs */
  private var _codecs: List<Codec>? = null

  /** Supported formats */
  private var _formats: List<Format>? = null

  /** Supported pixel formats */
  private var _pixelFormats: List<PixelFormat>? = null

  /** Supported filters */
  private var _filters: List<Filter>? = null

  /** Supported channel layouts */
  private var _channelLayouts: List<ChannelLayout>? = null


  /**
   * Returns true if the binary we are using is the true ffmpeg. This is to avoid conflict with
   * avconv (from the libav project), that some symlink to ffmpeg.
   *
   * @return true iff this is the official ffmpeg binary.
   * @throws IOException If a I/O error occurs while executing ffmpeg.
   */
  @Throws(IOException::class)
  fun isFFmpeg(): Boolean {
    return version().startsWith("ffmpeg")
  }

  /**
   * Throws an exception if this is an unsupported version of ffmpeg.
   *
   * @throws IllegalArgumentException if this is not the official ffmpeg binary.
   * @throws IOException If a I/O error occurs while executing ffmpeg.
   */
  @Throws(IllegalArgumentException::class, IOException::class)
  private fun checkIfFFmpeg() {
    if (!isFFmpeg()) {
      throw IllegalArgumentException(
          "This binary '$path' is not a supported version of ffmpeg")
    }
  }

  @Synchronized
  @Throws(IOException::class)
  fun codecs(): List<Codec> {
    checkIfFFmpeg()

    if (this._codecs == null) {
      val codecsList = mutableListOf<Codec>()
      val p = runFunc.run(ImmutableList.of(path, "-codecs"))
      try {
        val r = wrapInReader(p)
        var line: String?
        while (run { line = r.readLine(); line } != null) {
          val m = CODECS_REGEX.matcher(line!!)
          if (!m.matches()) continue
          codecsList.add(Codec(m.group(2), m.group(3), m.group(1)))
        }
        throwOnError(p)
        this._codecs = ImmutableList.copyOf(codecsList)
      } finally {
        p.destroy()
      }
    }
    return this._codecs!!
  }

  @Synchronized
  @Throws(IOException::class)
  fun filters(): List<Filter> {
    checkIfFFmpeg()

    if (this._filters == null) {
      val filtersList = mutableListOf<Filter>()
      val p = runFunc.run(ImmutableList.of(path, "-filters"))
      try {
        val r = wrapInReader(p)
        var line: String?
        while (run { line = r.readLine(); line } != null) {
          val m = FILTERS_REGEX.matcher(line!!)
          if (!m.matches()) continue
          filtersList.add(
              Filter(
                  m.group("timelinesupport") == "T",
                  m.group("slicethreading") == "S",
                  m.group("commandsupport") == "C",
                  m.group("name"),
                  FilterPattern(m.group("inputpattern")),
                  FilterPattern(m.group("outputpattern")),
                  m.group("description")))
        }
        throwOnError(p)
        this._filters = ImmutableList.copyOf(filtersList)
      } finally {
        p.destroy()
      }
    }
    return this._filters!!
  }

  @Synchronized
  @Throws(IOException::class)
  fun formats(): List<Format> {
    checkIfFFmpeg()

    if (this._formats == null) {
      val formatsList = mutableListOf<Format>()
      val p = runFunc.run(ImmutableList.of(path, "-formats"))
      try {
        val r = wrapInReader(p)
        var line: String?
        while (run { line = r.readLine(); line } != null) {
          val m = FORMATS_REGEX.matcher(line!!)
          if (!m.matches()) continue
          formatsList.add(Format(m.group(2), m.group(3), m.group(1)))
        }
        throwOnError(p)
        this._formats = ImmutableList.copyOf(formatsList)
      } finally {
        p.destroy()
      }
    }
    return this._formats!!
  }

  @Synchronized
  @Throws(IOException::class)
  fun pixelFormats(): List<PixelFormat> {
    checkIfFFmpeg()

    if (this._pixelFormats == null) {
      val pixelFormatsList = mutableListOf<PixelFormat>()
      val p = runFunc.run(ImmutableList.of(path, "-pix_fmts"))
      try {
        val r = wrapInReader(p)
        var line: String?
        while (run { line = r.readLine(); line } != null) {
          val m = PIXEL_FORMATS_REGEX.matcher(line!!)
          if (!m.matches()) continue
          val flags = m.group(1)
          pixelFormatsList.add(
              PixelFormat(
                  m.group(2), m.group(3).toInt(), m.group(4).toInt(), flags))
        }
        throwOnError(p)
        this._pixelFormats = ImmutableList.copyOf(pixelFormatsList)
      } finally {
        p.destroy()
      }
    }
    return this._pixelFormats!!
  }

  @Synchronized
  @Throws(IOException::class)
  fun channelLayouts(): List<ChannelLayout> {
    checkIfFFmpeg()

    if (this._channelLayouts == null) {
      val p = runFunc.run(ImmutableList.of(path, "-layouts"))
      try {
        val r = wrapInReader(p)
        // parseLayouts returns a Java list, ensure it's immutable and potentially a Kotlin list
        this._channelLayouts = InfoParser.parseLayouts(r).toList()
        
        // Ensuring process completed successfully after parsing, as original code didn't throwOnError.
        val exitStatus = p.waitFor()
        if (exitStatus != 0) {
            CharStreams.copy(wrapErrorInReader(p), processErrorStream) // Log error stream
            throw IOException("$path -layouts returned non-zero exit status: $exitStatus. Check error stream.")
        }

      } finally {
        p.destroy()
      }
    }
    return this._channelLayouts!!
  }

  @Throws(IOException::class)
  protected fun createProgressParser(listener: ProgressListener): ProgressParser {
    return try {
      TcpProgressParser(checkNotNull(listener))
    } catch (e: URISyntaxException) {
      throw IOException(e)
    }
  }

  @Throws(IOException::class)
  override fun run(args: List<String>) {
    checkIfFFmpeg()
    super.run(args)
  }

  @Throws(IOException::class)
  fun run(builder: FFmpegBuilder) {
    run(builder, null)
  }

  @Throws(IOException::class)
  fun run(builder: FFmpegBuilder, listener: ProgressListener?) {
    checkNotNull(builder)
    if (listener != null) {
      createProgressParser(listener).use { progressParser ->
        progressParser.start()
        // Ensure builder is treated as a new instance if addProgress modifies it and returns a new one
        val finalBuilder = builder.addProgress(progressParser.uri)
        run(finalBuilder.build())
      }
    } else {
      run(builder.build())
    }
  }

  fun builder(): FFmpegBuilder {
    return FFmpegBuilder()
  }

  companion object {
    const val FFMPEG = "ffmpeg"
    @JvmField // For Java compatibility
    val DEFAULT_PATH: String = MoreObjects.firstNonNull(System.getenv("FFMPEG"), FFMPEG)

    @JvmField val FPS_30: Fraction = Fraction.getFraction(30, 1)
    @JvmField val FPS_29_97: Fraction = Fraction.getFraction(30000, 1001)
    @JvmField val FPS_24: Fraction = Fraction.getFraction(24, 1)
    @JvmField val FPS_23_976: Fraction = Fraction.getFraction(24000, 1001)

    const val AUDIO_MONO = 1
    const val AUDIO_STEREO = 2

    const val AUDIO_FORMAT_U8 = "u8"
    const val AUDIO_FORMAT_S16 = "s16"
    const val AUDIO_FORMAT_S32 = "s32"
    const val AUDIO_FORMAT_FLT = "flt"
    const val AUDIO_FORMAT_DBL = "dbl"

    @Deprecated("Use AUDIO_FORMAT_U8") @JvmField val AUDIO_DEPTH_U8 = AUDIO_FORMAT_U8
    @Deprecated("Use AUDIO_FORMAT_S16") @JvmField val AUDIO_DEPTH_S16 = AUDIO_FORMAT_S16
    @Deprecated("Use AUDIO_FORMAT_S32") @JvmField val AUDIO_DEPTH_S32 = AUDIO_FORMAT_S32
    @Deprecated("Use AUDIO_FORMAT_FLT") @JvmField val AUDIO_DEPTH_FLT = AUDIO_FORMAT_FLT
    @Deprecated("Use AUDIO_FORMAT_DBL") @JvmField val AUDIO_DEPTH_DBL = AUDIO_FORMAT_DBL

    const val AUDIO_SAMPLE_8000 = 8000
    const val AUDIO_SAMPLE_11025 = 11025
    const val AUDIO_SAMPLE_12000 = 12000
    const val AUDIO_SAMPLE_16000 = 16000
    const val AUDIO_SAMPLE_22050 = 22050
    const val AUDIO_SAMPLE_32000 = 32000
    const val AUDIO_SAMPLE_44100 = 44100
    const val AUDIO_SAMPLE_48000 = 48000
    const val AUDIO_SAMPLE_96000 = 96000

    @JvmField internal val CODECS_REGEX: Pattern = Pattern.compile("^ ([.D][.E][VASD][.I][.L][.S]) (\\S{2,})\\s+(.*)$")
    @JvmField internal val FORMATS_REGEX: Pattern = Pattern.compile("^ ([ D][ E]) (\\S+)\\s+(.*)$")
    @JvmField internal val PIXEL_FORMATS_REGEX: Pattern = Pattern.compile("^([.I][.O][.H][.P][.B]) (\\S{2,})\\s+(\\d+)\\s+(\\d+)$")
    @JvmField internal val FILTERS_REGEX: Pattern = Pattern.compile(
        "^\\s*(?<timelinesupport>[T.])(?<slicethreading>[S.])(?<commandsupport>[C.])\\s(?<name>[A-Za-z0-9_]+)\\s+(?<inputpattern>[AVN|]+)->(?<outputpattern>[AVN|]+)\\s+(?<description>.*)$")
  }
}
