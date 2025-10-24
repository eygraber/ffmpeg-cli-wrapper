package net.bramp.ffmpeg

import com.google.common.io.ByteStreams
import com.google.common.io.CountingOutputStream
import com.google.common.net.HostAndPort
import net.bramp.ffmpeg.FFmpeg.FPS_30
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.builder.Strict
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.job.FFmpegJob
import net.bramp.ffmpeg.progress.Progress
import net.bramp.ffmpeg.progress.RecordingProgressListener
import org.glassfish.grizzly.PortRange
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.http.util.MimeType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.core.Is.`is`
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.rules.Timeout
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Tests actually shelling out ffmpeg and ffprobe. Could be flakey if ffmpeg or ffprobe change. */
class FFmpegExecutorTest {

  @get:Rule
  val timeout = Timeout(30, TimeUnit.SECONDS)

  private val ffmpeg = FFmpeg()
  private val ffprobe = FFprobe(FFprobe.DEFAULT_PATH)
  private val ffExecutor = FFmpegExecutor(ffmpeg, ffprobe)
  private val executor: ExecutorService = Executors.newSingleThreadExecutor()

  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class)
  fun testNormal() {
    val builder = FFmpegBuilder()
      .setVerbosity(FFmpegBuilder.Verbosity.Debug)
      .setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36"
      )
      .setInput(getWebserverRoot() + Samples.base_big_buck_bunny_720p_1mb)
      .addExtraArgs("-probesize", "1000000")
      // .setStartOffset(1500, TimeUnit.MILLISECONDS)
      .done()
      .overrideOutputFiles(true)
      .addOutput(Samples.output_mp4)
      .setFrames(100)
      .setFormat("mp4")
      .setStartOffset(500, TimeUnit.MILLISECONDS)
      .setAudioCodec("aac")
      .setAudioChannels(1)
      .setAudioSampleRate(48000)
      .setAudioBitStreamFilter("chomp")
      .setAudioFilter("aecho=0.8:0.88:6:0.4")
      .setAudioQuality(1)
      .setVideoCodec("libx264")
      .setVideoFrameRate(FPS_30)
      .setVideoResolution(320, 240)
      // .setVideoFilter("scale=320:trunc(ow/a/2)*2")
      // .setVideoPixelFormat("yuv420p")
      // .setVideoBitStreamFilter("noise")
      .setVideoQuality(2)
      .setStrict(Strict.Experimental)
      .done()

    val job = ffExecutor.createJob(builder)
    runAndWait(job)

    assertEquals(FFmpegJob.State.Finished, job.state)
  }

  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class)
  fun testTwoPass() {
    val input = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)
    assertFalse(input.hasError())

    val builder = FFmpegBuilder()
      .setInput(input)
      .done()
      .overrideOutputFiles(true)
      .addOutput(Samples.output_mp4)
      .setFormat("mp4")
      .disableAudio()
      .setVideoCodec("mpeg4")
      .setVideoFrameRate(FFmpeg.FPS_30)
      .setVideoResolution(320, 240)
      .setTargetSize(1024 * 1024)
      .done()

    val job = ffExecutor.createTwoPassJob(builder)
    runAndWait(job)

    assertEquals(FFmpegJob.State.Finished, job.state)
  }

  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class)
  fun testFilter() {
    val builder = FFmpegBuilder()
      .setInput(Samples.big_buck_bunny_720p_1mb)
      .done()
      .overrideOutputFiles(true)
      .addOutput(Samples.output_mp4)
      .setFormat("mp4")
      .disableAudio()
      .setVideoCodec("mpeg4")
      .setVideoFilter("scale=320:trunc(ow/a/2)*2")
      .done()

    val job = ffExecutor.createJob(builder)
    runAndWait(job)

    assertEquals(FFmpegJob.State.Finished, job.state)
  }

  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class)
  fun testMetaTags() {
    val builder = FFmpegBuilder()
      .setInput(Samples.big_buck_bunny_720p_1mb)
      .done()
      .overrideOutputFiles(true)
      .addOutput(Samples.output_mp4)
      .setFormat("mp4")
      .disableAudio()
      .setVideoCodec("mpeg4")
      .addMetaTag("comment", "This=Nice!")
      .addMetaTag("title", "Big Buck Bunny")
      .done()

    val job = ffExecutor.createJob(builder)
    runAndWait(job)

    assertEquals(FFmpegJob.State.Finished, job.state)
  }

  /** Test if addStdoutOutput() actually works, and the output can be correctly captured. */
  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class)
  fun testStdout() {
    val builder = FFmpegBuilder()
      .setInput(Samples.big_buck_bunny_720p_1mb)
      .done()
      .addStdoutOutput()
      .setFormat("s8")
      .setAudioChannels(1)
      .done()

    val newArgs = listOf(ffmpeg.path) + builder.build()

    // TODO Add support to the FFmpegJob to export the stream
    val p = ProcessBuilder(newArgs).start()

    val out = CountingOutputStream(ByteStreams.nullOutputStream())
    ByteStreams.copy(p.inputStream, out)

    assertEquals(0, p.waitFor())

    // This is perhaps fragile, but one byte per audio sample
    assertEquals(254976, out.count)
  }

  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class)
  fun testProgress() {
    val input = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)

    assertFalse(input.hasError())

    val builder = FFmpegBuilder()
      .setInput(input)
      .readAtNativeFrameRate() // Slows the test down
      .done()
      .overrideOutputFiles(true)
      .addOutput(Samples.output_mp4)
      .done()

    val listener = RecordingProgressListener()

    val job = ffExecutor.createJob(builder, listener)
    runAndWait(job)

    assertEquals(FFmpegJob.State.Finished, job.state)

    val progresses = listener.progresses

    // Since the results of ffmpeg are not predictable, test for the bare minimum.
    assertThat(progresses, hasSize(greaterThanOrEqualTo(2)))
    assertThat(progresses[0].status, `is`(Progress.Status.Continue))
    assertThat(progresses[progresses.size - 1].status, `is`(Progress.Status.End))
  }

  @Test
  fun testIssue112() {
    val builder = FFmpegBuilder()
      .addInput(Samples.testscreen_jpg)
      .addExtraArgs("-loop", "1")
      .done()
      .addInput(Samples.test_mp3)
      .done()
      .overrideOutputFiles(true)
      .addOutput(Samples.output_mp4)
      .setFormat("mp4")
      // .setDuration(30, TimeUnit.SECONDS)
      .addExtraArgs("-shortest")
      .setAudioCodec("aac")
      .setAudioSampleRate(48_000)
      .setAudioBitRate(32768)
      .setVideoCodec("libx264")
      .setVideoFrameRate(24, 1)
      .setVideoResolution(640, 480)
      .setStrict(Strict.Experimental) // Allow FFmpeg to use experimental specs
      .done()

    // Run a one-pass encode
    ffExecutor.createJob(builder).run()
  }

  @Throws(ExecutionException::class, InterruptedException::class)
  private fun runAndWait(job: FFmpegJob) {
    executor.submit(job).get()
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(FFmpegExecutorTest::class.java)

    // Webserver which can be used for fetching files over HTTP
    private lateinit var server: HttpServer

    @JvmStatic
    @BeforeClass
    @Throws(IOException::class)
    fun startWebserver() {
      MimeType.add("mp4", "video/mp4")

      server = HttpServer.createSimpleServer(
        Samples.TEST_PREFIX, "127.0.0.1", PortRange(10000, 60000)
      )
      server.start()

      LOG.info("Started server at {}", getWebserverRoot())
    }

    @JvmStatic
    @AfterClass
    fun stopWebserver() {
      server.shutdownNow()
    }

    private fun getWebserverRoot(): String {
      val net = server.getListener("grizzly")
      val hp = HostAndPort.fromParts(net.host, net.port)
      return "http://$hp/"
    }
  }
}
