package net.bramp.ffmpeg.info

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

data class IndividualChannel(override val name: String, val description: String) : ChannelLayout {

  override fun toString(): String = "$name $description"

  override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

  override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}
