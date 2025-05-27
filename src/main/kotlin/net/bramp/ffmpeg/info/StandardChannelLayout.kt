package net.bramp.ffmpeg.info

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

data class StandardChannelLayout(
  override val name: String,
  val decomposition: List<IndividualChannel>,
) : ChannelLayout {

  override fun toString(): String = name

  override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

  override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}
