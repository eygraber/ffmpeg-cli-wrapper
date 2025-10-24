package net.bramp.ffmpeg

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.bramp.ffmpeg.builder.FFprobeBuilder
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.lang.MockProcess
import net.bramp.ffmpeg.probe.FFmpegError
import net.bramp.ffmpeg.probe.FFmpegFrame
import net.bramp.ffmpeg.probe.FFmpegProbeResult
import net.bramp.ffmpeg.shared.CodecType
import org.apache.commons.lang3.math.Fraction
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class FFprobeTest {
  private lateinit var runFunc: ProcessFunction
  private lateinit var mockProcess: Process
  private lateinit var ffprobe: FFprobe

  @Before
  fun before() {
    runFunc = mockk()
    mockProcess = mockk()

    // Default response for any unmatched call (including -version)
    every { runFunc.run(any()) } answers { MockProcess(Helper.loadResource("ffprobe-version")) }

    // Specific responses for sample files
    every { runFunc.run(match { list -> list.any { it.contains(Samples.big_buck_bunny_720p_1mb) } }) } returns
      MockProcess(
        Helper.loadResource(
          "ffprobe-big_buck_bunny_720p_1mb.mp4",
        ),
      )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.always_on_my_mind) } }) } returns MockProcess(
      Helper.loadResource("ffprobe-Always On My Mind [Program Only] - Adelen.mp4"),
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.start_pts_test) } }) } returns MockProcess(
      Helper.loadResource(
        "ffprobe-start_pts_test",
      ),
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.divide_by_zero) } }) } returns MockProcess(
      Helper.loadResource(
        "ffprobe-divide-by-zero",
      ),
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.book_with_chapters) } }) } returns MockProcess(
      Helper.loadResource("book_with_chapters.m4b"),
    )
    every {
      runFunc.run(match { list -> list.any { it.contains(Samples.big_buck_bunny_720p_1mb_with_packets) } })
    } returns
      MockProcess(
        Helper.loadResource("ffprobe-big_buck_bunny_720p_1mb_packets.mp4"),
      )
    every {
      runFunc.run(match { list -> list.any { it.contains(Samples.big_buck_bunny_720p_1mb_with_frames) } })
    } returns
      MockProcess(
        Helper.loadResource(
          "ffprobe-big_buck_bunny_720p_1mb_frames.mp4",
        ),
      )
    every {
      runFunc.run(match { list -> list.any { it.contains(Samples.big_buck_bunny_720p_1mb_with_packets_and_frames) } })
    } returns MockProcess(
      Helper.loadResource("ffprobe-big_buck_bunny_720p_1mb_packets_and_frames.mp4"),
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.side_data_list) } }) } returns MockProcess(
      Helper.loadResource(
        "ffprobe-side_data_list",
      ),
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.chapters_with_long_id) } }) } returns
      MockProcess(
        Helper.loadResource(
          "chapters_with_long_id.m4b",
        ),
      )

    ffprobe = FFprobe("ffprobe", runFunc)
  }

  @Test
  fun testVersion() {
    ffprobe.version() shouldBe "ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers"
    ffprobe.version() shouldBe "ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers"
  }

  @Test
  fun testProbeVideo() {
    val info = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)
    info.hasError() shouldBe false

    info.streams!!.size shouldBe 2
    info.streams!![0].codecType shouldBe CodecType.Video
    info.streams!![1].codecType shouldBe CodecType.Audio

    info.streams!![1].channels shouldBe 6
    info.streams!![1].sampleRate shouldBe 48_000

    info.chapters!!.isEmpty() shouldBe true
  }

  @Test
  fun testProbeBookWithChapters() {
    val info = ffprobe.probe(Samples.book_with_chapters)
    info.hasError() shouldBe false
    info.chapters!!.size shouldBe 24

    val firstChapter = info.chapters!![0]
    firstChapter.timeBase shouldBe "1/44100"
    firstChapter.start shouldBe 0L
    firstChapter.startTime shouldBe "0.000000"
    firstChapter.end shouldBe 11_951_309L
    firstChapter.endTime shouldBe "271.004739"
    firstChapter.tags?.title shouldBe "01 - Sammy Jay Makes a Fuss"

    val lastChapter = info.chapters!![info.chapters!!.size - 1]
    lastChapter.timeBase shouldBe "1/44100"
    lastChapter.start shouldBe 237_875_790L
    lastChapter.startTime shouldBe "5394.008844"
    lastChapter.end shouldBe 248_628_224L
    lastChapter.endTime shouldBe "5637.828209"
    lastChapter.tags?.title shouldBe "24 - Chatterer Has His Turn to Laugh"
  }

  @Test
  fun testProbeWithPackets() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets)
        .setShowPackets(true)
        .build(),
    )
    info.hasError() shouldBe false
    info.packets!!.size shouldBe 381

    val firstPacket = info.packets!![0]
    firstPacket.codecType shouldBe CodecType.Audio
    firstPacket.streamIndex shouldBe 1
    firstPacket.pts shouldBe 0L
    firstPacket.ptsTime shouldBe "0.000000"
    firstPacket.dts shouldBe 0L
    firstPacket.dtsTime shouldBe "0.000000"
    firstPacket.duration shouldBe 1024L
    firstPacket.durationTime shouldBe "0.021333"
    firstPacket.size shouldBe "967"
    firstPacket.pos shouldBe "4261"
    firstPacket.flags shouldBe "K_"

    val secondPacket = info.packets!![1]
    secondPacket.codecType shouldBe CodecType.Video
    secondPacket.streamIndex shouldBe 0
    secondPacket.pts shouldBe 0L
    secondPacket.ptsTime shouldBe "0.000000"
    secondPacket.dts shouldBe 0L
    secondPacket.dtsTime shouldBe "0.000000"
    secondPacket.duration shouldBe 512L
    secondPacket.durationTime shouldBe "0.040000"
    secondPacket.size shouldBe "105222"
    secondPacket.pos shouldBe "5228"
    secondPacket.flags shouldBe "K_"

    val lastPacket = info.packets!![info.packets!!.size - 1]
    lastPacket.codecType shouldBe CodecType.Audio
    lastPacket.streamIndex shouldBe 1
    lastPacket.pts shouldBe 253_952L
    lastPacket.ptsTime shouldBe "5.290667"
    lastPacket.dts shouldBe 253_952L
    lastPacket.dtsTime shouldBe "5.290667"
    lastPacket.duration shouldBe 1024L
    lastPacket.durationTime shouldBe "0.021333"
    lastPacket.size shouldBe "1111"
    lastPacket.pos shouldBe "1054609"
    lastPacket.flags shouldBe "K_"
  }

  @Test
  fun testProbeWithFrames() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_frames)
        .setShowFrames(true)
        .build(),
    )
    info.hasError() shouldBe false
    info.frames!!.size shouldBe 381

    val firstFrame = info.frames!![0]
    firstFrame.streamIndex shouldBe 1
    firstFrame.keyFrame shouldBe 1
    firstFrame.pktPts shouldBe 0L
    firstFrame.pktPtsTime shouldBe "0.000000"
    firstFrame.pktDts shouldBe 0L
    firstFrame.pktDtsTime shouldBe "0.000000"
    firstFrame.bestEffortTimestamp shouldBe 0L
    firstFrame.bestEffortTimestampTime shouldBe "0.000000"
    firstFrame.pktDuration shouldBe 1024L
    firstFrame.pktDurationTime shouldBe "0.021333"
    firstFrame.pktPos shouldBe 4261L
    firstFrame.pktSize shouldBe 967L
    firstFrame.sampleFmt shouldBe "fltp"
    firstFrame.nbSamples shouldBe 1024
    firstFrame.channels shouldBe 6
    firstFrame.channelLayout shouldBe "5.1"

    val secondFrame = info.frames!![1]
    secondFrame.mediaType shouldBe CodecType.Video
    secondFrame.streamIndex shouldBe 0
    secondFrame.keyFrame shouldBe 1
    secondFrame.pktPts shouldBe 0L
    secondFrame.pktPtsTime shouldBe "0.000000"
    secondFrame.pktDts shouldBe 0L
    secondFrame.pktDtsTime shouldBe "0.000000"
    secondFrame.bestEffortTimestamp shouldBe 0L
    secondFrame.bestEffortTimestampTime shouldBe "0.000000"
    secondFrame.pktDuration shouldBe 512L
    secondFrame.pktDurationTime shouldBe "0.040000"
    secondFrame.pktPos shouldBe 5228L
    secondFrame.pktSize shouldBe 105_222L
    secondFrame.sampleFmt shouldBe null
    secondFrame.nbSamples shouldBe 0
    secondFrame.channels shouldBe 0
    secondFrame.channelLayout shouldBe null

    val lastFrame = info.frames!![info.frames!!.size - 1]
    assertLastFrame(lastFrame)
  }

  @Test
  fun testProbeWithPacketsAndFrames() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets_and_frames)
        .setShowPackets(true)
        .setShowFrames(true)
        .build(),
    )
    info.hasError() shouldBe false
    info.packets!!.size shouldBe 381
    info.frames!!.size shouldBe 381

    val firstPacket = info.packets!![0]
    firstPacket.codecType shouldBe CodecType.Audio
    firstPacket.streamIndex shouldBe 1
    firstPacket.pts shouldBe 0L
    firstPacket.ptsTime shouldBe "0.000000"
    firstPacket.dts shouldBe 0L
    firstPacket.dtsTime shouldBe "0.000000"
    firstPacket.duration shouldBe 1024L
    firstPacket.durationTime shouldBe "0.021333"
    firstPacket.size shouldBe "967"
    firstPacket.pos shouldBe "4261"
    firstPacket.flags shouldBe "K_"

    val secondPacket = info.packets!![1]
    secondPacket.codecType shouldBe CodecType.Video
    secondPacket.streamIndex shouldBe 0
    secondPacket.pts shouldBe 0L
    secondPacket.ptsTime shouldBe "0.000000"
    secondPacket.dts shouldBe 0L
    secondPacket.dtsTime shouldBe "0.000000"
    secondPacket.duration shouldBe 512L
    secondPacket.durationTime shouldBe "0.040000"
    secondPacket.size shouldBe "105222"
    secondPacket.pos shouldBe "5228"
    secondPacket.flags shouldBe "K_"

    val lastPacket = info.packets!![info.packets!!.size - 1]
    lastPacket.codecType shouldBe CodecType.Audio
    lastPacket.streamIndex shouldBe 1
    lastPacket.pts shouldBe 253_952L
    lastPacket.ptsTime shouldBe "5.290667"
    lastPacket.dts shouldBe 253_952L
    lastPacket.dtsTime shouldBe "5.290667"
    lastPacket.duration shouldBe 1024L
    lastPacket.durationTime shouldBe "0.021333"
    lastPacket.size shouldBe "1111"
    lastPacket.pos shouldBe "1054609"
    lastPacket.flags shouldBe "K_"

    val firstFrame = info.frames!![0]
    firstFrame.streamIndex shouldBe 1
    firstFrame.keyFrame shouldBe 1
    firstFrame.pktPts shouldBe 0L
    firstFrame.pktPtsTime shouldBe "0.000000"
    firstFrame.pktDts shouldBe 0L
    firstFrame.pktDtsTime shouldBe "0.000000"
    firstFrame.bestEffortTimestamp shouldBe 0L
    firstFrame.bestEffortTimestampTime shouldBe "0.000000"
    firstFrame.pktDuration shouldBe 1024L
    firstFrame.pktDurationTime shouldBe "0.021333"
    firstFrame.pktPos shouldBe 4261L
    firstFrame.pktSize shouldBe 967L
    firstFrame.sampleFmt shouldBe "fltp"
    firstFrame.nbSamples shouldBe 1024
    firstFrame.channels shouldBe 6
    firstFrame.channelLayout shouldBe "5.1"

    val secondFrame = info.frames!![1]
    secondFrame.mediaType shouldBe CodecType.Video
    secondFrame.streamIndex shouldBe 0
    secondFrame.keyFrame shouldBe 1
    secondFrame.pktPts shouldBe 0L
    secondFrame.pktPtsTime shouldBe "0.000000"
    secondFrame.pktDts shouldBe 0L
    secondFrame.pktDtsTime shouldBe "0.000000"
    secondFrame.bestEffortTimestamp shouldBe 0L
    secondFrame.bestEffortTimestampTime shouldBe "0.000000"
    secondFrame.pktDuration shouldBe 512L
    secondFrame.pktDurationTime shouldBe "0.040000"
    secondFrame.pktPos shouldBe 5228L
    secondFrame.pktSize shouldBe 105_222L
    secondFrame.sampleFmt shouldBe null
    secondFrame.nbSamples shouldBe 0
    secondFrame.channels shouldBe 0
    secondFrame.channelLayout shouldBe null

    val lastFrame = info.frames!![info.frames!!.size - 1]
    assertLastFrame(lastFrame)
  }

  private fun assertLastFrame(actual: FFmpegFrame) {
    actual.mediaType shouldBe CodecType.Audio
    actual.streamIndex shouldBe 1
    actual.keyFrame shouldBe 1
    actual.pktPts shouldBe 253_952L
    actual.pktPtsTime shouldBe "5.290667"
    actual.pktDts shouldBe 253_952L
    actual.pktDtsTime shouldBe "5.290667"
    actual.bestEffortTimestamp shouldBe 253_952L
    actual.bestEffortTimestampTime shouldBe "5.290667"
    actual.pktDuration shouldBe 1024L
    actual.pktDurationTime shouldBe "0.021333"
    actual.pktPos shouldBe 1_054_609L
    actual.pktSize shouldBe 1111L
    actual.sampleFmt shouldBe "fltp"
    actual.nbSamples shouldBe 1024
    actual.channels shouldBe 6
    actual.channelLayout shouldBe "5.1"
  }

  @Test
  fun testProbeVideo2() {
    val info = ffprobe.probe(Samples.always_on_my_mind)
    info.hasError() shouldBe false

    info.streams!!.size shouldBe 2
    info.streams!![0].codecType shouldBe CodecType.Video
    info.streams!![1].codecType shouldBe CodecType.Audio

    info.streams!![1].channels shouldBe 2
    info.streams!![1].sampleRate shouldBe 48_000

    info.format!!.filename shouldBe "c:\\Users\\Bob\\Always On My Mind [Program Only] - Adelén.mp4"
  }

  @Test
  fun testProbeStartPts() {
    val info = ffprobe.probe(Samples.start_pts_test)
    info.hasError() shouldBe false

    info.streams!![0].startPts shouldBe 8_570_867_078L
  }

  @Test
  fun testProbeDivideByZero() {
    val info = ffprobe.probe(Samples.divide_by_zero)
    info.hasError() shouldBe false

    info.streams!![1].codecTimeBase shouldBe Fraction.ZERO
  }

  @Test
  fun shouldThrowOnErrorWithFFmpegProbeResult() {
    every { mockProcess.exitValue() } answers { 1 }

    val error = FFmpegError()
    val result = FFmpegProbeResult(error)

    val exception = shouldThrow<FFmpegException> {
      ffprobe.throwOnError(mockProcess, result)
    }
    exception.error shouldBe error
  }

  @Test
  fun shouldThrowOnErrorEvenIfProbeResultHasNoError() {
    every { mockProcess.exitValue() } answers { 1 }

    val result = FFmpegProbeResult()
    val exception = shouldThrow<FFmpegException> {
      ffprobe.throwOnError(mockProcess, result)
    }
    exception.error shouldBe null
  }

  @Test
  fun shouldThrowOnErrorEvenIfProbeResultIsNull() {
    every { mockProcess.exitValue() } answers { 1 }

    val exception = shouldThrow<FFmpegException> {
      ffprobe.throwOnError(mockProcess, null)
    }
    exception.error shouldBe null
  }

  @Test
  fun testShouldThrowErrorWithoutMock() {
    val probe = FFprobe(FFprobe.DEFAULT_PATH)
    val exception = shouldThrow<FFmpegException> {
      probe.probe("doesnotexist.mp4")
    }

    exception shouldNotBe null
    exception.error shouldNotBe null
    exception.error?.string shouldNotBe null
    exception.error?.code shouldNotBe 0
  }

  @Test
  fun testProbeSideDataList() {
    val info = ffprobe.probe(Samples.side_data_list)

    info.streams!![0].sideDataList.size shouldBe 1
    info.streams!![0].sideDataList[0].sideDataType shouldBe "Display Matrix"
    info.streams!![0].sideDataList[0].displayMatrix shouldBe
      "\n00000000:            0      -65536           0\n00000001:        65536           0           0\n00000002:            0           0  1073741824\n"
    info.streams!![0].sideDataList[0].rotation shouldBe 90
  }

  @Test
  fun testChaptersWithLongIds() {
    val info = ffprobe.probe(Samples.chapters_with_long_id)

    info.chapters!![0].id shouldBe 6_613_449_456_311_024_506L
    info.chapters!![1].id shouldBe -4_433_436_293_284_298_339L
  }

  @Test
  fun testProbeDefaultArguments() {
    ffprobe.probe(Samples.always_on_my_mind)

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    value shouldBe listOf(
      "-v", "quiet",
      "-print_format", "json",
      "-show_error",
      "-show_format",
      "-show_streams",
      "-show_chapters",
      Samples.always_on_my_mind,
    )
  }

  @Test
  fun testProbeProbeBuilder() {
    ffprobe.probe(FFprobeBuilder().setInput(Samples.always_on_my_mind))

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    value shouldBe listOf(
      "-v", "quiet",
      "-print_format", "json",
      "-show_error",
      "-show_format",
      "-show_streams",
      "-show_chapters",
      Samples.always_on_my_mind,
    )
  }

  @Test
  fun testProbeProbeBuilderBuilt() {
    ffprobe.probe(FFprobeBuilder().setInput(Samples.always_on_my_mind).build())

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    value shouldBe listOf(
      "-v", "quiet",
      "-print_format", "json",
      "-show_error",
      "-show_format",
      "-show_streams",
      "-show_chapters",
      Samples.always_on_my_mind,
    )
  }

  @Test
  fun testProbeProbeExtraArgs() {
    ffprobe.probe(Samples.always_on_my_mind, null, "-rw_timeout", "0")

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    value shouldBe listOf(
      "-v", "quiet",
      "-print_format", "json",
      "-show_error",
      "-rw_timeout", "0",
      "-show_format",
      "-show_streams",
      "-show_chapters",
      Samples.always_on_my_mind,
    )
  }

  @Test
  fun testProbeProbeUserAgent() {
    ffprobe.probe(Samples.always_on_my_mind, "ffmpeg-cli-wrapper")

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    value shouldBe listOf(
      "-v", "quiet",
      "-print_format", "json",
      "-show_error",
      "-user_agent", "ffmpeg-cli-wrapper",
      "-show_format",
      "-show_streams",
      "-show_chapters",
      Samples.always_on_my_mind,
    )
  }

  @Test
  fun testFullFormatDeserialization() {
    val format = ffprobe.probe(Samples.always_on_my_mind).format!!

    format.filename shouldEndWith "Always On My Mind [Program Only] - Adelén.mp4"
    format.nbStreams shouldBe 2
    format.nbPrograms shouldBe 0
    format.formatName shouldBe "mov,mp4,m4a,3gp,3g2,mj2"
    format.formatLongName shouldBe "QuickTime / MOV"
    format.startTime shouldBe 0.0
    format.duration shouldBe 181.632
    format.size shouldBe 417_127_573
    format.bitRate shouldBe 18_372_426
    format.probeScore shouldBe 100
    format.tags?.size shouldBe 4

    format.tags!!["major_brand"] shouldBe "mp42"
  }

  @Test
  fun testFullChaptersDeserialization() {
    val chapters = ffprobe.probe(Samples.book_with_chapters).chapters!!
    val chapter = chapters[chapters.size - 1]

    chapters.size shouldBe 24

    chapter.id shouldBe 23
    chapter.timeBase shouldBe "1/44100"
    chapter.start shouldBe 237_875_790
    chapter.startTime shouldBe "5394.008844"
    chapter.end shouldBe 248_628_224
    chapter.endTime shouldBe "5637.828209"
    chapter.tags?.title shouldBe "24 - Chatterer Has His Turn to Laugh"
  }

  @Test
  fun testFullVideoStreamDeserialization() {
    val streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).streams!!
    val stream = streams[0]

    stream.index shouldBe 0
    stream.codecName shouldBe "h264"
    stream.codecLongName shouldBe "H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10"
    stream.profile shouldBe "Main"
    stream.codecType shouldBe CodecType.Video
    stream.codecTimeBase shouldBe Fraction.getFraction(1, 50)
    stream.codecTagString shouldBe "avc1"
    stream.codecTag shouldBe "0x31637661"
    stream.width shouldBe 1280
    stream.height shouldBe 720
    stream.hasBFrames shouldBe 0
    stream.sampleAspectRatio shouldBe "1:1"
    stream.displayAspectRatio shouldBe "16:9"
    stream.pixFmt shouldBe "yuv420p"
    stream.level shouldBe 31
    stream.chromaLocation shouldBe "left"
    stream.refs shouldBe 1
    stream.isAvc shouldBe "true"
    stream.nalLengthSize shouldBe "4"
    stream.id shouldBe "0x1"
    stream.rFrameRate shouldBe Fraction.getFraction(25, 1)
    stream.avgFrameRate shouldBe Fraction.getFraction(25, 1)
    stream.timeBase shouldBe Fraction.getFraction(1, 12_800)
    stream.startPts shouldBe 0
    stream.startTime shouldBe 0.0
    stream.durationTs shouldBe 67_584
    stream.duration shouldBe 5.28
    stream.bitRate shouldBe 1_205_959
    stream.maxBitRate shouldBe 0
    stream.bitsPerRawSample shouldBe 8
    stream.bitsPerSample shouldBe 0
    stream.nbFrames shouldBe 132
    stream.sampleFmt shouldBe null
    stream.sampleRate shouldBe 0
    stream.channels shouldBe 0
    stream.channelLayout shouldBe null
    stream.tags?.size shouldBe 4
    stream.tags!!["language"] shouldBe "und"
    stream.sideDataList.size shouldBe 0
  }

  @Test
  fun testFullAudioStreamDeserialization() {
    val streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).streams!!
    val stream = streams[1]

    stream.index shouldBe 1
    stream.codecName shouldBe "aac"
    stream.codecLongName shouldBe "AAC (Advanced Audio Coding)"
    stream.profile shouldBe "LC"
    stream.codecType shouldBe CodecType.Audio
    stream.codecTimeBase shouldBe Fraction.getFraction(1, 48_000)
    stream.codecTagString shouldBe "mp4a"
    stream.codecTag shouldBe "0x6134706d"
    stream.width shouldBe 0
    stream.height shouldBe 0
    stream.hasBFrames shouldBe 0
    stream.sampleAspectRatio shouldBe null
    stream.displayAspectRatio shouldBe null
    stream.pixFmt shouldBe null
    stream.level shouldBe 0
    stream.chromaLocation shouldBe null
    stream.refs shouldBe 0
    stream.isAvc shouldBe null
    stream.nalLengthSize shouldBe null
    stream.id shouldBe "0x2"
    stream.rFrameRate shouldBe Fraction.getFraction(0, 1)
    stream.avgFrameRate shouldBe Fraction.getFraction(0, 1)
    stream.timeBase shouldBe Fraction.getFraction(1, 48_000)
    stream.startPts shouldBe 0
    stream.startTime shouldBe 0.0
    stream.durationTs shouldBe 254_976
    stream.duration shouldBe 5.312
    stream.bitRate shouldBe 384_828
    stream.maxBitRate shouldBe 400_392
    stream.bitsPerRawSample shouldBe 0
    stream.bitsPerSample shouldBe 0
    stream.nbFrames shouldBe 249
    stream.sampleFmt shouldBe "fltp"
    stream.sampleRate shouldBe 48_000
    stream.channels shouldBe 6
    stream.channelLayout shouldBe "5.1"
    stream.tags?.size shouldBe 4
    stream.tags!!["language"] shouldBe "und"
    stream.sideDataList.size shouldBe 0
  }

  @Test
  fun testSideDataListDeserialization() {
    val streams = ffprobe.probe(Samples.side_data_list).streams!!
    val sideDataList = streams[0].sideDataList

    sideDataList.size shouldBe 1
    sideDataList[0].sideDataType shouldBe "Display Matrix"
    sideDataList[0].rotation shouldBe 90
    sideDataList[0].displayMatrix shouldStartWith "\n00000000:"
  }

  @Test
  fun testDispositionDeserialization() {
    val streams = ffprobe.probe(Samples.side_data_list).streams!!
    val disposition = streams[0].disposition!!

    disposition.isDefault() shouldBe true
    disposition.isDub() shouldBe false
    disposition.isOriginal() shouldBe false
    disposition.isComment() shouldBe false
    disposition.isLyrics() shouldBe false
    disposition.isKaraoke() shouldBe false
    disposition.isForced() shouldBe false
    disposition.isHearingImpaired() shouldBe false
    disposition.isVisualImpaired() shouldBe false
    disposition.isCleanEffects() shouldBe false
    disposition.isAttachedPic() shouldBe false
    disposition.isCaptions() shouldBe false
    disposition.isDescriptions() shouldBe false
    disposition.isMetadata() shouldBe false
  }

  @Ignore("Broken until we fix mocking in Kotlin")
  @Test
  fun testDispositionWithAllFieldsTrueDeserialization() {
    val streams = ffprobe.probe(Samples.disposition_all_true).streams!!
    val disposition = streams[0].disposition!!

    disposition.isDefault() shouldBe true
    disposition.isDub() shouldBe true
    disposition.isOriginal() shouldBe true
    disposition.isComment() shouldBe true
    disposition.isLyrics() shouldBe true
    disposition.isKaraoke() shouldBe true
    disposition.isForced() shouldBe true
    disposition.isHearingImpaired() shouldBe true
    disposition.isVisualImpaired() shouldBe true
    disposition.isCleanEffects() shouldBe true
    disposition.isAttachedPic() shouldBe true
    disposition.isTimedThumbnails() shouldBe true
    disposition.isNonDiegetic() shouldBe true
    disposition.isCaptions() shouldBe true
    disposition.isDescriptions() shouldBe true
    disposition.isMetadata() shouldBe true
    disposition.isDependent() shouldBe true
    disposition.isStillImage() shouldBe true
  }

  @Test
  fun testFullPacketDeserialization() {
    val probeBuilder = ffprobe.builder()
      .setShowPackets(true)
      .setInput(Samples.big_buck_bunny_720p_1mb_with_packets)
    val packets = ffprobe.probe(probeBuilder).packets!!

    val packet = packets[packets.size - 1]

    packet.codecType shouldBe CodecType.Audio
    packet.streamIndex shouldBe 1
    packet.pts shouldBe 253_952
    packet.ptsTime shouldBe "5.290667"
    packet.dts shouldBe 253_952
    packet.dtsTime shouldBe "5.290667"
    packet.duration shouldBe 1024
    packet.durationTime shouldBe "0.021333"
    packet.size shouldBe "1111"
    packet.pos shouldBe "1054609"
    packet.flags shouldBe "K_"
  }

  @Test
  fun testFullFrameDeserialization() {
    val probeBuilder = ffprobe.builder()
      .setShowFrames(true)
      .setInput(Samples.big_buck_bunny_720p_1mb_with_frames)
    val frames = ffprobe.probe(probeBuilder).frames!!

    val frame = frames[frames.size - 1]

    frame.mediaType shouldBe CodecType.Audio
    frame.streamIndex shouldBe 1
    frame.keyFrame shouldBe 1
    frame.pktPts shouldBe 253_952
    frame.pktPtsTime shouldBe "5.290667"
    frame.pktDts shouldBe 253_952
    frame.pktDtsTime shouldBe "5.290667"
    frame.bestEffortTimestamp shouldBe 253_952
    frame.bestEffortTimestampTime shouldBe "5.290667"
    frame.pktDuration shouldBe 1024
    frame.pktDurationTime shouldBe "0.021333"
    frame.pktPos shouldBe 1_054_609
    frame.pktSize shouldBe 1111
    frame.sampleFmt shouldBe "fltp"
    frame.nbSamples shouldBe 1024
    frame.channels shouldBe 6
    frame.channelLayout shouldBe "5.1"
  }
}
