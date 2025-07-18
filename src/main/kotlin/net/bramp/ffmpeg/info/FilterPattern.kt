package net.bramp.ffmpeg.info

import net.bramp.ffmpeg.shared.CodecType
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import java.util.Arrays

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

  override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

  override fun toString(): String {
    if(isSinkOrSource) {
      return "|"
    }

    return if(isVariableStreams) {
      "N"
    }
    else {
      Arrays.toString(streams.toTypedArray())
    }
  }

  override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

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
