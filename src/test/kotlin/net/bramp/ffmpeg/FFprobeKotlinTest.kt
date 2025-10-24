package net.bramp.ffmpeg

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.lang.MockProcess
import net.bramp.ffmpeg.shared.CodecType
import org.apache.commons.lang3.math.Fraction
import org.junit.After
import org.junit.Before
import org.junit.Test

class FFprobeKotlinTest {

  private lateinit var runFunc: ProcessFunction
  private lateinit var ffprobe: FFprobe

  @Before
  fun setup() {
    runFunc = mockk()
    setupMocks()
    ffprobe = FFprobe("ffprobe", runFunc)
  }

  @After
  fun tearDown() {
    clearAllMocks()
  }

  private fun setupMocks() {
    every { runFunc.run(match { it.contains("-version") }) } answers {
      MockProcess(Helper.loadResource("ffprobe-version"))
    }

    every {
      runFunc.run(
        match {
          it.contains(Samples.big_buck_bunny_720p_1mb) && !it.contains("packets") && !it.contains("frames")
        },
      )
    } answers {
      MockProcess(Helper.loadResource("ffprobe-big_buck_bunny_720p_1mb.mp4"))
    }

    every { runFunc.run(match { it.contains(Samples.always_on_my_mind) }) } answers {
      MockProcess(Helper.loadResource("ffprobe-Always On My Mind [Program Only] - Adelen.mp4"))
    }

    every { runFunc.run(match { it.contains(Samples.book_with_chapters) }) } answers {
      MockProcess(Helper.loadResource("book_with_chapters.m4b"))
    }

    every { runFunc.run(match { it.contains(Samples.big_buck_bunny_720p_1mb_with_packets) }) } answers {
      MockProcess(Helper.loadResource("ffprobe-big_buck_bunny_720p_1mb_packets.mp4"))
    }

    every { runFunc.run(match { it.contains(Samples.big_buck_bunny_720p_1mb_with_frames) }) } answers {
      MockProcess(Helper.loadResource("ffprobe-big_buck_bunny_720p_1mb_frames.mp4"))
    }

    every { runFunc.run(match { it.contains(Samples.big_buck_bunny_720p_1mb_with_packets_and_frames) }) } answers {
      MockProcess(Helper.loadResource("ffprobe-big_buck_bunny_720p_1mb_packets_and_frames.mp4"))
    }

    every { runFunc.run(match { it.contains(Samples.divide_by_zero) }) } answers {
      MockProcess(Helper.loadResource("ffprobe-divide-by-zero"))
    }

    every { runFunc.run(match { it.contains(Samples.chapters_with_long_id) }) } answers {
      MockProcess(Helper.loadResource("chapters_with_long_id.m4b"))
    }
  }

  @Test
  fun `testVersion - should return first line and call run once per version call`() {
    val version1 = ffprobe.version()
    val version2 = ffprobe.version()

    version1 shouldBe "ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers"
    version2 shouldBe "ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers"

    verify(exactly = 2) { runFunc.run(match { it.contains("-version") }) }
  }

  @Test
  fun `testProbeVideo - should probe video file successfully`() {
    val info = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)
    info.hasError() shouldBe false

    info.streams!! shouldHaveSize 2
    info.streams!![0].codecType shouldBe CodecType.Video
    info.streams!![1].codecType shouldBe CodecType.Audio
    info.streams!![1].channels shouldBe 6
    info.streams!![1].sampleRate shouldBe 48_000
  }

  @Test
  fun `testProbeBookWithChapters - should correctly parse chapters`() {
    val info = ffprobe.probe(Samples.book_with_chapters)
    info.hasError() shouldBe false
    info.chapters?.size shouldBe 24

    val firstChapter = info.chapters!![0]
    firstChapter.timeBase shouldBe "1/44100"
    firstChapter.start shouldBe 0L
    firstChapter.startTime shouldBe "0.000000"
    firstChapter.end shouldBe 11_951_309L
  }

  @Test
  fun `testProbeWithPackets - should parse packets correctly`() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets)
        .setShowPackets(true),
    )

    info.hasError() shouldBe false
    info.packets?.size shouldBe 381

    val firstPacket = info.packets!![0]
    firstPacket.codecType shouldBe CodecType.Audio
    firstPacket.streamIndex shouldBe 1
  }

  @Test
  fun `testProbeWithFrames - should parse frames correctly`() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_frames)
        .setShowFrames(true),
    )

    info.hasError() shouldBe false
    info.frames?.size shouldBe 381

    val firstFrame = info.frames!![0]
    firstFrame.streamIndex shouldBe 1
    firstFrame.keyFrame shouldBe 1
  }

  @Test
  fun `testProbeWithPacketsAndFrames - should parse both packets and frames`() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets_and_frames)
        .setShowPackets(true)
        .setShowFrames(true),
    )

    info.hasError() shouldBe false
    info.packets?.size shouldBe 381
    info.frames?.size shouldBe 381
  }

  @Test
  fun `testProbeDivideByZero - should handle 0 by 0 fractions`() {
    val info = ffprobe.probe(Samples.divide_by_zero)
    info.hasError() shouldBe false

    info.streams!![1].codecTimeBase shouldBe Fraction.ZERO
  }

  @Test
  fun `testProbeDefaultArguments - should use default arguments`() {
    ffprobe.probe(Samples.always_on_my_mind)

    verify(atLeast = 1) {
      runFunc.run(
        match { args ->
          args.contains("-show_format") &&
          args.contains("-show_streams") &&
          args.contains("-show_chapters") &&
          args.contains(Samples.always_on_my_mind)
        },
      )
    }
  }

  @Test
  fun `testProbeProbeBuilder - should work with builder`() {
    ffprobe.probe(ffprobe.builder().setInput(Samples.always_on_my_mind))

    verify(atLeast = 1) {
      runFunc.run(
        match { args ->
          args.contains("-show_format") &&
          args.contains("-show_streams") &&
          args.contains(Samples.always_on_my_mind)
        },
      )
    }
  }

  @Test
  fun `testProbeProbeBuilderBuilt - should work with built builder`() {
    ffprobe.probe(ffprobe.builder().setInput(Samples.always_on_my_mind).build())

    verify(atLeast = 1) {
      runFunc.run(
        match { args ->
          args.contains(Samples.always_on_my_mind)
        },
      )
    }
  }

  @Test
  fun `testProbeProbeExtraArgs - should include extra arguments`() {
    ffprobe.probe(Samples.always_on_my_mind, null, "-rw_timeout", "0")

    verify(atLeast = 1) {
      runFunc.run(
        match { args ->
          args.contains("-rw_timeout") &&
          args.contains("0") &&
          args.contains(Samples.always_on_my_mind)
        },
      )
    }
  }

  @Test
  fun `testProbeProbeUserAgent - should include user agent`() {
    ffprobe.probe(Samples.always_on_my_mind, "ffmpeg-cli-wrapper")

    verify(atLeast = 1) {
      runFunc.run(
        match { args ->
          args.contains("-user_agent") &&
          args.contains("ffmpeg-cli-wrapper") &&
          args.contains(Samples.always_on_my_mind)
        },
      )
    }
  }

  @Test
  fun `testFullVideoStreamDeserialization - should deserialize all video stream properties`() {
    val streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).streams!!
    val stream = streams[0]

    stream.index shouldBe 0
    stream.codecName shouldBe "h264"
    stream.codecType shouldBe CodecType.Video
    stream.width shouldBe 1280
    stream.height shouldBe 720
    stream.pixFmt shouldBe "yuv420p"
    stream.isAvc shouldBe "true"
    stream.nalLengthSize shouldBe "4"
  }

  @Test
  fun `testFullAudioStreamDeserialization - should deserialize all audio stream properties`() {
    val streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).streams!!
    val stream = streams[1]

    stream.index shouldBe 1
    stream.codecName shouldBe "aac"
    stream.codecType shouldBe CodecType.Audio
    stream.sampleFmt shouldBe "fltp"
    stream.sampleRate shouldBe 48_000
    stream.channels shouldBe 6
    stream.channelLayout shouldBe "5.1"
  }

  @Test
  fun `testFullChaptersDeserialization - should deserialize all chapter properties`() {
    val chapters = ffprobe.probe(Samples.book_with_chapters).chapters!!
    val lastChapter = chapters[chapters.size - 1]

    chapters.size shouldBe 24
    lastChapter.id shouldBe 23L
    lastChapter.timeBase shouldBe "1/44100"
    lastChapter.start shouldBe 237_875_790L
    lastChapter.end shouldBe 248_628_224L
  }

  @Test
  fun `testFullPacketDeserialization - should deserialize all packet properties`() {
    val packets = ffprobe.probe(
      ffprobe.builder()
        .setShowPackets(true)
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets),
    ).packets!!

    val lastPacket = packets[packets.size - 1]
    lastPacket.codecType shouldBe CodecType.Audio
    lastPacket.streamIndex shouldBe 1
    lastPacket.pts shouldBe 253_952L
  }

  @Test
  fun `testShouldThrowErrorWithoutMock - should throw on nonexistent file`() {
    shouldThrow<FFmpegException> {
      val realProbe = FFprobe(FFprobe.DEFAULT_PATH)
      realProbe.probe("doesnotexist.mp4")
    }
  }
}
