package net.bramp.ffmpeg.info

data class PixelFormat(
  val name: String,
  val numberOfComponents: Int,
  val bitsPerPixel: Int,
  val canDecode: Boolean,
  val canEncode: Boolean,
  val isHardwareAccelerated: Boolean,
  val isPalettedFormat: Boolean,
  val isBitstreamFormat: Boolean,
) {
  constructor(name: String, numberOfComponents: Int, bitsPerPixel: Int, flags: String) : this(
    name,
    numberOfComponents,
    bitsPerPixel,
    flags[0] == 'I',
    flags[1] == 'O',
    flags[2] == 'H',
    flags[3] == 'P',
    flags[4] == 'B',
  )

  override fun toString(): String = name
}
