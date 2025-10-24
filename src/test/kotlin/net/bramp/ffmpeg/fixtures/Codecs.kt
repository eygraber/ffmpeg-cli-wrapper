package net.bramp.ffmpeg.fixtures

import net.bramp.ffmpeg.info.Codec

object Codecs {
  val CODECS = listOf(
    Codec("012v", "Uncompressed 4:2:2 10-bit", "D.VI.S"),
    Codec("4xm", "4X Movie", "D.V.L."),
    Codec("8bps", "QuickTime 8BPS video", "D.VI.S"),
    Codec("a64_multi", "Multicolor charset for Commodore 64 (encoders: a64multi )", ".EVIL."),
    Codec(
      "a64_multi5",
      "Multicolor charset for Commodore 64, extended with 5th color (colram) (encoders: a64multi5 )",
      ".EVIL."
    ),
    Codec("aasc", "Autodesk RLE", "D.V..S"),
    Codec("agm", "Amuse Graphics Movie", "D.V.L."),
    Codec("aic", "Apple Intermediate Codec", "D.VIL."),
    Codec("alias_pix", "Alias/Wavefront PIX image", "DEVI.S"),
    Codec("amv", "AMV Video", "DEVIL."),
    // ... ADD THE REST OF THE CODECS FROM THE JAVA VERSION ...
  )
}
