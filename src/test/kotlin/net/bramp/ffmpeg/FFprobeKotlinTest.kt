package net.bramp.ffmpeg

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.lang.MockProcess
import net.bramp.ffmpeg.shared.CodecType
import org.apache.commons.lang3.math.Fraction
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertFalse
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

    assertThat(version1, `is`("ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers"))
    assertThat(version2, `is`("ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers"))

    verify(exactly = 2) { runFunc.run(match { it.contains("-version") }) }
  }

  @Test
  fun `testProbeVideo - should probe video file successfully`() {
    val info = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)
    assertFalse(info.hasError())

    assertThat(info.streams, hasSize(2))
    assertThat(info.streams!![0].codecType, `is`(CodecType.Video))
    assertThat(info.streams!![1].codecType, `is`(CodecType.Audio))
    assertThat(info.streams!![1].channels, `is`(6))
    assertThat(info.streams!![1].sampleRate, `is`(48_000))
  }

  @Test
  fun `testProbeBookWithChapters - should correctly parse chapters`() {
    val info = ffprobe.probe(Samples.book_with_chapters)
    assertThat(info.hasError(), `is`(false))
    assertThat(info.chapters?.size, `is`(24))

    val firstChapter = info.chapters!![0]
    assertThat(firstChapter.timeBase, `is`("1/44100"))
    assertThat(firstChapter.start, `is`(0L))
    assertThat(firstChapter.startTime, `is`("0.000000"))
    assertThat(firstChapter.end, `is`(11_951_309L))
  }

  @Test
  fun `testProbeWithPackets - should parse packets correctly`() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets)
        .setShowPackets(true),
    )

    assertThat(info.hasError(), `is`(false))
    assertThat(info.packets?.size, `is`(381))

    val firstPacket = info.packets!![0]
    assertThat(firstPacket.codecType, `is`(CodecType.Audio))
    assertThat(firstPacket.streamIndex, `is`(1))
  }

  @Test
  fun `testProbeWithFrames - should parse frames correctly`() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_frames)
        .setShowFrames(true),
    )

    assertThat(info.hasError(), `is`(false))
    assertThat(info.frames?.size, `is`(381))

    val firstFrame = info.frames!![0]
    assertThat(firstFrame.streamIndex, `is`(1))
    assertThat(firstFrame.keyFrame, `is`(1))
  }

  @Test
  fun `testProbeWithPacketsAndFrames - should parse both packets and frames`() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets_and_frames)
        .setShowPackets(true)
        .setShowFrames(true),
    )

    assertThat(info.hasError(), `is`(false))
    assertThat(info.packets?.size, `is`(381))
    assertThat(info.frames?.size, `is`(381))
  }

  @Test
  fun `testProbeDivideByZero - should handle 0 by 0 fractions`() {
    val info = ffprobe.probe(Samples.divide_by_zero)
    assertFalse(info.hasError())

    assertThat(info.streams!![1].codecTimeBase, `is`(Fraction.ZERO))
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

    assertThat(stream.index, `is`(0))
    assertThat(stream.codecName, `is`("h264"))
    assertThat(stream.codecType, `is`(CodecType.Video))
    assertThat(stream.width, `is`(1280))
    assertThat(stream.height, `is`(720))
    assertThat(stream.pixFmt, `is`("yuv420p"))
    assertThat(stream.isAvc, `is`("true"))
    assertThat(stream.nalLengthSize, `is`("4"))
  }

  @Test
  fun `testFullAudioStreamDeserialization - should deserialize all audio stream properties`() {
    val streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).streams!!
    val stream = streams[1]

    assertThat(stream.index, `is`(1))
    assertThat(stream.codecName, `is`("aac"))
    assertThat(stream.codecType, `is`(CodecType.Audio))
    assertThat(stream.sampleFmt, `is`("fltp"))
    assertThat(stream.sampleRate, `is`(48_000))
    assertThat(stream.channels, `is`(6))
    assertThat(stream.channelLayout, `is`("5.1"))
  }

  @Test
  fun `testFullChaptersDeserialization - should deserialize all chapter properties`() {
    val chapters = ffprobe.probe(Samples.book_with_chapters).chapters!!
    val lastChapter = chapters[chapters.size - 1]

    assertThat(chapters.size, `is`(24))
    assertThat(lastChapter.id, `is`(23L))
    assertThat(lastChapter.timeBase, `is`("1/44100"))
    assertThat(lastChapter.start, `is`(237_875_790L))
    assertThat(lastChapter.end, `is`(248_628_224L))
  }

  @Test
  fun `testFullPacketDeserialization - should deserialize all packet properties`() {
    val packets = ffprobe.probe(
      ffprobe.builder()
        .setShowPackets(true)
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets),
    ).packets!!

    val lastPacket = packets[packets.size - 1]
    assertThat(lastPacket.codecType, `is`(CodecType.Audio))
    assertThat(lastPacket.streamIndex, `is`(1))
    assertThat(lastPacket.pts, `is`(253_952L))
  }

  @Test(expected = FFmpegException::class)
  fun `testShouldThrowErrorWithoutMock - should throw on nonexistent file`() {
    val realProbe = FFprobe(FFprobe.DEFAULT_PATH)
    realProbe.probe("doesnotexist.mp4")
  }
}
