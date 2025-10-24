package net.bramp.ffmpeg.nut

import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.math.Fraction
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.nio.charset.StandardCharsets
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioFormat.Encoding.ALAW
import javax.sound.sampled.AudioFormat.Encoding.PCM_FLOAT
import javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED
import javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED
import javax.sound.sampled.AudioFormat.Encoding.ULAW

@RunWith(Parameterized::class)
class RawHandlerStreamToAudioFormatTest(
  fourcc: String,
  sampleRateNum: Int,
  sampleRateDenom: Int,
  channels: Int,
  private val expected: AudioFormat,
) {
  private val stream: StreamHeaderPacket = StreamHeaderPacket().apply {
    type = StreamHeaderPacket.AUDIO.toLong()
    this.fourcc = fourcc.toByteArray(StandardCharsets.ISO_8859_1)
    sampleRate = Fraction.getFraction(sampleRateNum, sampleRateDenom)
    this.channels = channels
  }

  @Test
  fun testStreamToAudioFormat() {
    val format = RawHandler.streamToAudioFormat(stream)

    // Compare strings since AudioFormat does not have a good equalsCode(..) method.
    format.toString() shouldBe expected.toString()
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{4}")
    fun data(): List<Array<Any>> = listOf(
      arrayOf("ALAW", 48_000, 1, 2, AudioFormat(ALAW, 48_000f, 8, 2, 2, 48_000f, false)),
      arrayOf("ULAW", 48_000, 1, 3, AudioFormat(ULAW, 48_000f, 8, 3, 3, 48_000f, false)),
      arrayOf("PSD\u0008", 48_000, 1, 4, AudioFormat(PCM_SIGNED, 48_000f, 8, 4, 4, 48_000f, false)),
      arrayOf("\u0010DUP", 48_000, 1, 6, AudioFormat(PCM_UNSIGNED, 48_000f, 16, 6, 12, 48_000f, true)),
      arrayOf("PFD ", 48_000, 1, 8, AudioFormat(PCM_FLOAT, 48_000f, 32, 8, 32, 48_000f, false)),
    )
  }
}
