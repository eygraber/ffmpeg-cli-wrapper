package net.bramp.ffmpeg.info

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegTest.Companion.argThatHasItem
import net.bramp.ffmpeg.ProcessFunction
import net.bramp.ffmpeg.lang.NewProcessAnswer
import net.bramp.ffmpeg.shared.CodecType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class FFmpegGetInfoTest {
  @Mock
  private lateinit var runFunc: ProcessFunction

  @Before
  @Throws(IOException::class)
  fun before() {
    `when`(runFunc.run(argThatHasItem("-version")))
      .thenAnswer(NewProcessAnswer("ffmpeg-version"))

    `when`(runFunc.run(argThatHasItem("-codecs")))
      .thenAnswer(NewProcessAnswer("ffmpeg-codecs"))
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

    for(codec in ffmpeg.codecs()) {
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

    assertThat(videoCodecs, hasItem(hasProperty("name", equalTo("h264"))))
    assertThat(audioCodecs, hasItem(hasProperty("name", equalTo("aac"))))
    assertThat(subtitleCodecs, hasItem(hasProperty("name", equalTo("ssa"))))
    assertThat(dataCodecs, hasItem(hasProperty("name", equalTo("bin_data"))))
  }
}
