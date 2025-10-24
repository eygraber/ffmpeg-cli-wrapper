package net.bramp.ffmpeg.info

import io.mockk.*
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.Helper
import net.bramp.ffmpeg.ProcessFunction
import net.bramp.ffmpeg.lang.MockProcess
import net.bramp.ffmpeg.shared.CodecType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

class FFmpegGetInfoTest {
  private lateinit var runFunc: ProcessFunction

  @Before
  @Throws(IOException::class)
  fun before() {
    runFunc = mockk()
    every { runFunc.run(match { it.contains("-version") }) } returns MockProcess(Helper.loadResource("ffmpeg-version"))
    every { runFunc.run(match { it.contains("-codecs") }) } returns MockProcess(Helper.loadResource("ffmpeg-codecs"))
  }

  @Test
  @Throws(IOException::class)
  fun getFFmpegCodecSupportTest() {
    val videoCodecs = ArrayList<Codec>()
    val audioCodecs = ArrayList<Codec>()
    val subtitleCodecs = ArrayList<Codec>()
    val dataCodecs = ArrayList<Codec>()
    val otherCodecs = ArrayList<Codec>()

    val ffmpeg = FFmpeg("ffmpeg", runFunc)
    ffmpeg.codecs()

    for(codec in ffmpeg.codecs() ?: emptyList()) {
      when(codec.type) {
        CodecType.Video -> videoCodecs.add(codec)
        CodecType.Audio -> audioCodecs.add(codec)
        CodecType.Subtitle -> subtitleCodecs.add(codec)
        CodecType.Data -> dataCodecs.add(codec)
        else -> otherCodecs.add(codec)
      }
    }

    assertThat(videoCodecs, hasSize(245))
    assertThat(audioCodecs, hasSize(180))
    assertThat(subtitleCodecs, hasSize(26))
    assertThat(dataCodecs, hasSize(8))
    assertThat(otherCodecs, hasSize(0))

    assertThat(videoCodecs, hasItem<Codec>(hasProperty<Codec>("name", equalTo("h264"))))
    assertThat(audioCodecs, hasItem<Codec>(hasProperty<Codec>("name", equalTo("aac"))))
    assertThat(subtitleCodecs, hasItem<Codec>(hasProperty<Codec>("name", equalTo("ssa"))))
    assertThat(dataCodecs, hasItem<Codec>(hasProperty<Codec>("name", equalTo("bin_data"))))
  }
}
