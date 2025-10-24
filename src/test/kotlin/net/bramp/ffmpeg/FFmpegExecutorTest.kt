package net.bramp.ffmpeg

import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.FFmpeg.Companion.FPS_30
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.builder.Strict
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.job.FFmpegJob
import net.bramp.ffmpeg.progress.Progress
import net.bramp.ffmpeg.progress.RecordingProgressListener
import org.glassfish.grizzly.PortRange
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.http.util.MimeType
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Simple counting output stream that counts bytes written */
private class CountingOutputStream(private val delegate: OutputStream) : OutputStream() {
  var count: Long = 0
    private set

  override fun write(b: Int) {
    delegate.write(b)
    count++
  }

  override fun write(b: ByteArray) {
    delegate.write(b)
    count += b.size
  }

  override fun write(b: ByteArray, off: Int, len: Int) {
    delegate.write(b, off, len)
    count += len
  }

  override fun flush() = delegate.flush()
  override fun close() = delegate.close()
}

/** Simple null output stream that discards all data */
private class NullOutputStream : OutputStream() {
  override fun write(b: Int) {
    // Discard
  }

  override fun write(b: ByteArray) {
    // Discard
  }

  override fun write(b: ByteArray, off: Int, len: Int) {
    // Discard
  }
}

/** Simple host and port data class */
private data class HostAndPort(val host: String, val port: Int) {
  override fun toString() = "$host:$port"
}

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
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36",
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
      .setAudioSampleRate(48_000)
      .setAudioBitStreamFilter("chomp")
      .setAudioFilter("aecho=0.8:0.88:6:0.4")
      .setAudioQuality(1.0)
      .setVideoCodec("libx264")
      .setVideoFrameRate(FPS_30)
      .setVideoResolution(320, 240)
      // .setVideoFilter("scale=320:trunc(ow/a/2)*2")
      // .setVideoPixelFormat("yuv420p")
      // .setVideoBitStreamFilter("noise")
      .setVideoQuality(2.0)
      .setStrict(Strict.Experimental)
      .done()

    val job = ffExecutor.createJob(builder)
    runAndWait(job)

    job.state shouldBe FFmpegJob.State.Finished
  }

  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class)
  fun testTwoPass() {
    val input = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)
    input.hasError() shouldBe false

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

    job.state shouldBe FFmpegJob.State.Finished
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

    job.state shouldBe FFmpegJob.State.Finished
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

    job.state shouldBe FFmpegJob.State.Finished
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

    val out = CountingOutputStream(NullOutputStream())
    p.inputStream.copyTo(out)

    p.waitFor() shouldBe 0

    // This is perhaps fragile, but one byte per audio sample
    out.count shouldBe 254_976
  }

  @Test
  @Throws(InterruptedException::class, ExecutionException::class, IOException::class)
  fun testProgress() {
    val input = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)

    input.hasError() shouldBe false

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

    job.state shouldBe FFmpegJob.State.Finished

    val progresses = listener.progresses

    // Since the results of ffmpeg are not predictable, test for the bare minimum.
    progresses.size shouldBeGreaterThanOrEqualTo 2
    progresses[0].status shouldBe Progress.Status.Continue
    progresses[progresses.size - 1].status shouldBe Progress.Status.End
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
      .setAudioBitRate(32_768)
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
        Samples.TEST_PREFIX,
        "127.0.0.1",
        PortRange(10_000, 60_000),
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
      val hp = HostAndPort(net.host, net.port)
      return "http://$hp/"
    }
  }
}
