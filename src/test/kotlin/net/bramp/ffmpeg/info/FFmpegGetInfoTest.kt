package net.bramp.ffmpeg.info

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.Helper
import net.bramp.ffmpeg.ProcessFunction
import net.bramp.ffmpeg.lang.MockProcess
import net.bramp.ffmpeg.shared.CodecType
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

    videoCodecs shouldHaveSize 245
    audioCodecs shouldHaveSize 180
    subtitleCodecs shouldHaveSize 26
    dataCodecs shouldHaveSize 8
    otherCodecs shouldHaveSize 0

    videoCodecs.map { it.name } shouldContain "h264"
    audioCodecs.map { it.name } shouldContain "aac"
    subtitleCodecs.map { it.name } shouldContain "ssa"
    dataCodecs.map { it.name } shouldContain "bin_data"
  }
}
