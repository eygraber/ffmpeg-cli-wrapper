package net.bramp.ffmpeg.fixtures

import net.bramp.ffmpeg.info.PixelFormat

object PixelFormats {
  val PIXEL_FORMATS = listOf(
    PixelFormat("yuv420p", 3, 12, "IO..."),
    PixelFormat("yuyv422", 3, 16, "IO..."),
    PixelFormat("rgb24", 3, 24, "IO..."),
    PixelFormat("bgr24", 3, 24, "IO..."),
    // ... ADD THE REST OF THE PIXEL FORMATS FROM THE JAVA VERSION ...
  )
}
