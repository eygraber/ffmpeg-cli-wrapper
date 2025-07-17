package net.bramp.ffmpeg.info

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

data class Filter(
  /** Is timeline editing supported  */
  val isTimelineSupported: Boolean,
  /** Is slice based multi-threading supported  */
  val isSliceThreading: Boolean,
  /** Are there command line options  */
  val isCommandSupport: Boolean,
  /** The filters name  */
  val name: String,
  /** The input filter pattern  */
  val inputPattern: FilterPattern,
  /** The output filter pattern  */
  val outputPattern: FilterPattern,
  /** A short description of the filter  */
  val description: String,
) {

  override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

  override fun toString(): String = name

  override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}
