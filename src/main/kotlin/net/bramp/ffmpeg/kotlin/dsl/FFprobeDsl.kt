package net.bramp.ffmpeg.kotlin.dsl

import net.bramp.ffmpeg.kotlin.builder.FFprobeBuilder

/**
 * Creates an FFprobeBuilder using a Kotlin DSL.
 *
 * Example:
 * ```
 * val builder = ffprobe {
 *   input = "input.mp4"
 *   showFormat = true
 *   showStreams = true
 * }
 * ```
 */
fun ffprobe(block: FFprobeDsl.() -> Unit): FFprobeBuilder {
  val dsl = FFprobeDsl()
  dsl.block()
  return dsl.builder
}

/**
 * DSL class for building FFprobe commands
 */
@FFmpegDslMarker
@Suppress("BooleanPropertyNaming")
class FFprobeDsl {
  internal val builder = FFprobeBuilder()

  /**
   * Set the input file
   */
  var input: String? = null
    set(value) {
      field = value
      value?.let { builder.setInput(it) }
    }

  /**
   * Show format information (default: true)
   */
  var showFormat: Boolean = true
    set(value) {
      field = value
      builder.setShowFormat(value)
    }

  /**
   * Show streams information (default: true)
   */
  var showStreams: Boolean = true
    set(value) {
      field = value
      builder.setShowStreams(value)
    }

  /**
   * Show chapters information (default: true)
   */
  var showChapters: Boolean = true
    set(value) {
      field = value
      builder.setShowChapters(value)
    }

  /**
   * Show frames information (default: false)
   */
  var showFrames: Boolean = false
    set(value) {
      field = value
      builder.setShowFrames(value)
    }

  /**
   * Show packets information (default: false)
   */
  var showPackets: Boolean = false
    set(value) {
      field = value
      builder.setShowPackets(value)
    }

  /**
   * Set user agent string
   */
  var userAgent: String? = null
    set(value) {
      field = value
      value?.let { builder.setUserAgent(it) }
    }

  /**
   * Add extra arguments
   */
  fun extraArgs(vararg args: String) {
    builder.addExtraArgs(*args)
  }
}
