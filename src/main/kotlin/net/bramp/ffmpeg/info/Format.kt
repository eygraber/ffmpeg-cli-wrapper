package net.bramp.ffmpeg.info

import com.google.common.base.Preconditions
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Information about supported Format
 *
 * @author bramp
 */
data class Format(
  val name: String,
  val longName: String,
  val canDemux: Boolean,
  val canMux: Boolean,
) {

  /**
   * @param name short format name
   * @param longName long format name
   * @param flags is expected to be in the following format:
   * <pre>
   * D. = Demuxing supported
   * .E = Muxing supported
   * </pre>
   */
  constructor(name: String, longName: String, flags: String) : this(
    name.trim(),
    longName.trim(),
    canDemux(flags),
    canMux(flags),
  )

  override fun toString(): String = "$name $longName"

  override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

  override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)

  companion object {
    private fun canDemux(flags: String): Boolean {
      Preconditions.checkArgument(flags.length == 2, "Format flags is invalid '%s'", flags)
      return flags[0] == 'D'
    }

    private fun canMux(flags: String): Boolean {
      Preconditions.checkArgument(flags.length == 2, "Format flags is invalid '%s'", flags)
      return flags[1] == 'E'
    }
  }
}
