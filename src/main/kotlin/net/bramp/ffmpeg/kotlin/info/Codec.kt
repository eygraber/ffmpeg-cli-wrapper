package net.bramp.ffmpeg.kotlin.info

import net.bramp.ffmpeg.kotlin.shared.CodecType

/**
 * Information about supported Codecs
 *
 * @author bramp
 */
data class Codec(
  val name: String,
  val longName: String,
  /** Can I decode with this codec  */
  val canDecode: Boolean,
  /** Can I encode with this codec  */
  val canEncode: Boolean,
  /** What type of codec is this  */
  val type: CodecType,
  /** Intra frame only codec  */
  val isIntraFrameOnly: Boolean,
  /** Codec supports lossy compression  */
  val isLossyCompressionSupported: Boolean,
  /** Codec supports lossless compression  */
  val isLosslessCompressionSupported: Boolean,
) {

  /**
   * @param name short codec name
   * @param longName long codec name
   * @param flags is expected to be in the following format:
   * <pre>
   * D..... = Decoding supported
   * .E.... = Encoding supported
   * ..V... = Video codec
   * ..A... = Audio codec
   * ..S... = Subtitle codec
   * ..D... = Data codec
   * ..T... = Attachment codec
   * ...I.. = Intra frame-only codec
   * ....L. = Lossy compression
   * .....S = Lossless compression
   * </pre>
   */
  constructor(name: String, longName: String, flags: String) : this(
    name.trim(),
    longName.trim(),
    canDecode(flags),
    canEncode(flags),
    codecType(flags),
    isIntraFrameOnly(flags),
    supportsLossyCompression(flags),
    supportsLosslessCompression(flags),
  )

  override fun toString(): String = "$name $longName"

  companion object {
    private fun canDecode(flags: String): Boolean {
      require(flags.length == 6) { "Codec flags is invalid '$flags'" }
      return when(flags[0]) {
        'D' -> true
        '.' -> false
        else -> throw IllegalArgumentException("Invalid decoding value '${flags[0]}'")
      }
    }

    private fun canEncode(flags: String): Boolean {
      require(flags.length == 6) { "Codec flags is invalid '$flags'" }
      return when(flags[1]) {
        'E' -> true
        '.' -> false
        else -> throw IllegalArgumentException("Invalid encoding value '${flags[1]}'")
      }
    }

    private fun codecType(flags: String): CodecType {
      require(flags.length == 6) { "Codec flags is invalid '$flags'" }
      return when(flags[2]) {
        'V' -> CodecType.Video
        'A' -> CodecType.Audio
        'S' -> CodecType.Subtitle
        'D' -> CodecType.Data
        'T' -> CodecType.Attachment
        else -> throw IllegalArgumentException("Invalid codec type '${flags[2]}'")
      }
    }

    private fun isIntraFrameOnly(flags: String): Boolean {
      require(flags.length == 6) { "Codec flags is invalid '$flags'" }
      return when(flags[3]) {
        'I' -> true
        '.' -> false
        else -> throw IllegalArgumentException("Invalid encoding value '${flags[3]}'")
      }
    }

    private fun supportsLossyCompression(flags: String): Boolean {
      require(flags.length == 6) { "Codec flags is invalid '$flags'" }
      return when(flags[4]) {
        'L' -> true
        '.' -> false
        else -> throw IllegalArgumentException("Invalid lossy compression value '${flags[4]}'")
      }
    }

    private fun supportsLosslessCompression(flags: String): Boolean {
      require(flags.length == 6) { "Codec flags is invalid '$flags'" }
      return when(flags[5]) {
        'S' -> true
        '.' -> false
        else -> throw IllegalArgumentException(
          "Invalid lossless compression value '${flags[5]}'",
        )
      }
    }
  }
}
