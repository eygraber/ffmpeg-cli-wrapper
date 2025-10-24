package net.bramp.ffmpeg.modelmapper

import net.bramp.ffmpeg.builder.FFmpegOutputBuilder
import net.bramp.ffmpeg.options.AudioEncodingOptions
import net.bramp.ffmpeg.options.EncodingOptions
import net.bramp.ffmpeg.options.MainEncodingOptions
import net.bramp.ffmpeg.options.VideoEncodingOptions
import org.junit.Test

class MapperTest {

  @Test
  fun testMapping() {
    val main = MainEncodingOptions("mp4", 0L, null)
    val audio = AudioEncodingOptions(false, null, 0, 0, null, 0, 0.0)
    val video = VideoEncodingOptions(
      true, null, null, 320, 240, 1000, null, "scale='320:trunc(ow/a/2)*2'", null
    )

    val options = EncodingOptions(main, audio, video)

    val mappedObj = FFmpegOutputBuilder()

    Mapper.INSTANCE.map(options, mappedObj)

    // TODO Add actual test!
  }
}
