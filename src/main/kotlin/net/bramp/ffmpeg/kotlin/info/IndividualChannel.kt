package net.bramp.ffmpeg.kotlin.info

data class IndividualChannel(override val name: String, val description: String) : ChannelLayout {
  override fun toString(): String = "$name $description"
}
