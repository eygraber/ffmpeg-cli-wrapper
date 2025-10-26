package net.bramp.ffmpeg.kotlin.info

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
  override fun toString(): String = name
}
