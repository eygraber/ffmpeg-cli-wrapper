package net.bramp.ffmpeg.kotlin.nut

import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.kotlin.FFmpeg
import net.bramp.ffmpeg.kotlin.builder.FFmpegBuilder
import net.bramp.ffmpeg.kotlin.fixtures.Samples
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.SourceDataLine

// TODO fix "invalid packet checksum" when running test
class NutReaderTest {
  private val OUTPUT_AUDIO = false
  private val OUTPUT_IMAGES = false

  @get:Rule
  val timeout = Timeout(30, TimeUnit.SECONDS)

  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class, LineUnavailableException::class)
  fun testNutReader() {
    val args = FFmpegBuilder()
      .setInput(Samples.big_buck_bunny_720p_1mb)
      .done()
      .addStdoutOutput()
      .setFormat("nut")
      .setVideoCodec("rawvideo")
      // .setVideoPixelFormat("rgb24") // TODO make 24bit / channel work
      .setVideoPixelFormat("argb") // 8 bits per channel
      .setAudioCodec("pcm_s32le")
      .done()
      .build()

    val newArgs = listOf(FFmpeg.DEFAULT_PATH) + args

    val builder = ProcessBuilder(newArgs)
    val p = builder.start()

    NutReader(
      p.inputStream,
      object : NutReaderListener {
        var line: SourceDataLine? = null

        override fun stream(stream: Stream) {
          if(stream.header.type == StreamHeaderPacket.AUDIO.toLong()) {
            if(!OUTPUT_AUDIO) {
              return
            }

            if(line != null) {
              throw RuntimeException("Multiple audio streams not supported")
            }

            // Get System Audio Line
            try {
              line = AudioSystem.getSourceDataLine(null)

              val format = RawHandler.streamToAudioFormat(stream.header)
              line!!.open(format)
              line!!.start()

              LOG.debug("New audio stream: {}", format)
            }
            catch(e: LineUnavailableException) {
              LOG.debug("Failed to open audio device", e)
            }
          }
        }

        override fun frame(frame: Frame) {
          LOG.debug("{}", frame)

          val header = frame.stream.header

          if(header.type == StreamHeaderPacket.VIDEO.toLong()) {
            val img = RawHandler.toBufferedImage(frame)

            if(!OUTPUT_IMAGES) {
              return
            }

            try {
              ImageIO.write(img, "png", File(String.format(Locale.ROOT, "test-%08d.png", frame.pts)))
            }
            catch(e: IOException) {
              LOG.error("Failed to write png", e)
            }
          }
          else if(header.type == StreamHeaderPacket.AUDIO.toLong()) {
            line?.write(frame.data, 0, frame.data.size)
          }
        }
      },
    ).read()

    p.waitFor() shouldBe 0
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(NutReaderTest::class.java)
  }
}
