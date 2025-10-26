package net.bramp.ffmpeg.kotlin.info

import net.bramp.ffmpeg.kotlin.shared.CodecType

data class FilterPattern(
  /**
   * Indicates whether this pattern represents a source or a sink and therefore has no other options
   */
  val isSinkOrSource: Boolean,
  /** Indicates whether this pattern accepts a variable number of streams  */
  val isVariableStreams: Boolean,
  /** Contains a pattern matching the stream types supported  */
  val streams: List<CodecType>,
) {
  constructor(pattern: String) : this(
    pattern.contains("|"),
    pattern.contains("N"),
    parseStreams(pattern),
  )

  override fun toString(): String = when {
    isSinkOrSource -> "|"
    isVariableStreams -> "N"
    else -> streams.joinToString(prefix = "[", postfix = "]")
  }

  companion object {
    private fun parseStreams(pattern: String): List<CodecType> {
      val streams: MutableList<CodecType> = ArrayList()

      for(i in pattern.indices) {
        when (val c = pattern[i]) {
          '|',
          'N',
          -> {}
          'A' -> streams.add(CodecType.Audio)
          'V' -> streams.add(CodecType.Video)
          else -> check(false) { "Unsupported character in filter pattern $c" }
        }
      }

      return streams.toList()
    }
  }
}
