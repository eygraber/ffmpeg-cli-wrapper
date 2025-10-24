package net.bramp.ffmpeg.info

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

  companion object {
    private fun canDemux(flags: String): Boolean {
      require(flags.length == 2) { "Format flags is invalid '$flags'" }
      return flags[0] == 'D'
    }

    private fun canMux(flags: String): Boolean {
      require(flags.length == 2) { "Format flags is invalid '$flags'" }
      return flags[1] == 'E'
    }
  }
}
