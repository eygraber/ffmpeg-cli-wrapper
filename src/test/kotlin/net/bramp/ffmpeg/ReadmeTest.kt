package net.bramp.ffmpeg

import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.builder.Strict
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.job.FFmpegJob
import net.bramp.ffmpeg.progress.Progress
import net.bramp.ffmpeg.progress.ProgressListener
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/** Ensures the examples in the README continue to work. */
class ReadmeTest @Throws(IOException::class) constructor() {

  private val locale = Locale.US
  private val ffmpeg = FFmpeg()
  private val ffprobe = FFprobe(FFprobe.DEFAULT_PATH)

  @Test
  @Suppress("UNUSED_VARIABLE")
  @Throws(IOException::class)
  fun testCreateFF() {
    val ffmpeg = FFmpeg(FFmpeg.DEFAULT_PATH)
    val ffprobe = FFprobe(FFmpeg.DEFAULT_PATH)

    // Construct them, and do nothing with them
  }

  @Test
  @Throws(IOException::class)
  fun testVideoEncoding() {
    val inFilename = Samples.big_buck_bunny_720p_1mb
    val input = ffprobe.probe(inFilename)

    val builder = FFmpegBuilder()
      .setInput(inFilename) // Filename, or a FFmpegProbeResult
      .done()
      .setInput(input)
      .done()
      .overrideOutputFiles(true) // Override the output if it exists
      .addOutput("output.mp4") // Filename for the destination
      .setFormat("mp4") // Format is inferred from filename, or can be set
      .setTargetSize(250_000) // Aim for a 250KB file
      .disableSubtitle() // No subtiles
      .setAudioChannels(1) // Mono audio
      .setAudioCodec("aac") // using the aac codec
      .setAudioSampleRate(48_000) // at 48KHz
      .setAudioBitRate(32768) // at 32 kbit/s
      .setVideoCodec("libx264") // Video using x264
      .setVideoFrameRate(24, 1) // at 24 frames per second
      .setVideoResolution(640, 480) // at 640x480 resolution
      .setStrict(Strict.Experimental) // Allow FFmpeg to use experimental specs
      .done()

    val executor = FFmpegExecutor(ffmpeg, ffprobe)

    // Run a one-pass encode
    executor.createJob(builder).run()

    // Or run a two-pass encode (which is slower at the cost of better quality
    executor.createTwoPassJob(builder).run()
  }

  @Test
  @Throws(IOException::class)
  fun testGetMediaInformation() {
    val probeResult = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)

    val format = probeResult.format
    val line1 = String.format(
      locale,
      "File: '%s' ; Format: '%s' ; Duration: %.3fs",
      format.filename,
      format.formatLongName,
      format.duration
    )

    val stream = probeResult.streams!![0]
    val line2 = String.format(
      locale,
      "Codec: '%s' ; Width: %dpx ; Height: %dpx",
      stream.codecLongName,
      stream.width,
      stream.height
    )

    assertThat(
      line1,
      `is`(
        "File: 'src/test/resources/net/bramp/ffmpeg/samples/big_buck_bunny_720p_1mb.mp4' ; Format: 'QuickTime / MOV' ; Duration: 5.312s"
      )
    )
    assertThat(
      line2,
      `is`("Codec: 'H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10' ; Width: 1280px ; Height: 720px")
    )
  }

  @Test
  @Throws(IOException::class)
  fun testProgress() {
    val executor = FFmpegExecutor(ffmpeg, ffprobe)

    val input = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)

    val builder = FFmpegBuilder()
      .setInput(input) // Or filename
      .done()
      .addOutput("output.mp4")
      .done()

    val job = executor.createJob(
      builder,
      object : ProgressListener {
        // Using the FFmpegProbeResult determine the duration of the input
        val durationNs = input.format.duration * TimeUnit.SECONDS.toNanos(1)

        override fun progress(progress: Progress) {
          val percentage = progress.outTimeNs / durationNs

          // Print out interesting information about the progress
          println(
            String.format(
              locale,
              "[%.0f%%] status:%s frame:%d time:%s fps:%.0f speed:%.2fx",
              percentage * 100,
              progress.status,
              progress.frame,
              FFmpegUtils.toTimecode(progress.outTimeNs, TimeUnit.NANOSECONDS),
              progress.fps.doubleValue(),
              progress.speed
            )
          )
        }
      }
    )

    job.run()

    assertEquals(FFmpegJob.State.Finished, job.state)
  }
}
