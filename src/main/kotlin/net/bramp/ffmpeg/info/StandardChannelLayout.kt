package net.bramp.ffmpeg.info

data class StandardChannelLayout(
  override val name: String,
  val decomposition: List<IndividualChannel>,
) : ChannelLayout {
  override fun toString(): String = name
}
