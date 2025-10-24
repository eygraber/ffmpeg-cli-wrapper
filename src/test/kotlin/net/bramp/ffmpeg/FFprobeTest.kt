package net.bramp.ffmpeg

import io.mockk.*
import net.bramp.ffmpeg.builder.FFprobeBuilder
import net.bramp.ffmpeg.fixtures.Samples
import net.bramp.ffmpeg.lang.MockProcess
import net.bramp.ffmpeg.probe.*
import net.bramp.ffmpeg.shared.CodecType
import org.apache.commons.lang3.math.Fraction
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.hamcrest.core.IsNull
import org.junit.Assert.*
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
    every { runFunc.run(match { list -> list.any { it.contains(Samples.big_buck_bunny_720p_1mb) } }) } returns MockProcess(
      Helper.loadResource(
        "ffprobe-big_buck_bunny_720p_1mb.mp4"
      )
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.always_on_my_mind) } }) } returns MockProcess(
      Helper.loadResource("ffprobe-Always On My Mind [Program Only] - Adelen.mp4")
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.start_pts_test) } }) } returns MockProcess(
      Helper.loadResource(
        "ffprobe-start_pts_test"
      )
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.divide_by_zero) } }) } returns MockProcess(
      Helper.loadResource(
        "ffprobe-divide-by-zero"
      )
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.book_with_chapters) } }) } returns MockProcess(
      Helper.loadResource("book_with_chapters.m4b")
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.big_buck_bunny_720p_1mb_with_packets) } }) } returns MockProcess(
      Helper.loadResource("ffprobe-big_buck_bunny_720p_1mb_packets.mp4")
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.big_buck_bunny_720p_1mb_with_frames) } }) } returns MockProcess(
      Helper.loadResource(
        "ffprobe-big_buck_bunny_720p_1mb_frames.mp4"
      )
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.big_buck_bunny_720p_1mb_with_packets_and_frames) } }) } returns MockProcess(
      Helper.loadResource("ffprobe-big_buck_bunny_720p_1mb_packets_and_frames.mp4")
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.side_data_list) } }) } returns MockProcess(
      Helper.loadResource(
        "ffprobe-side_data_list"
      )
    )
    every { runFunc.run(match { list -> list.any { it.contains(Samples.chapters_with_long_id) } }) } returns MockProcess(
      Helper.loadResource(
        "chapters_with_long_id.m4b"
      )
    )

    ffprobe = FFprobe("ffprobe", runFunc)
  }

  @Test
  fun testVersion() {
    assertEquals("ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers", ffprobe.version())
    assertEquals("ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers", ffprobe.version())
  }

  @Test
  fun testProbeVideo() {
    val info = ffprobe.probe(Samples.big_buck_bunny_720p_1mb)
    assertFalse(info.hasError())

    assertThat(info.streams, hasSize(2))
    assertThat(info.streams!![0].codecType, `is`(CodecType.Video))
    assertThat(info.streams!![1].codecType, `is`(CodecType.Audio))

    assertThat(info.streams!![1].channels, `is`(6))
    assertThat(info.streams!![1].sampleRate, `is`(48_000))

    assertThat(info.chapters!!.isEmpty(), `is`(true))
  }

  @Test
  fun testProbeBookWithChapters() {
    val info = ffprobe.probe(Samples.book_with_chapters)
    assertThat(info.hasError(), `is`(false))
    assertThat(info.chapters!!.size, `is`(24))

    val firstChapter = info.chapters!![0]
    assertThat(firstChapter.timeBase, `is`("1/44100"))
    assertThat(firstChapter.start, `is`(0L))
    assertThat(firstChapter.startTime, `is`("0.000000"))
    assertThat(firstChapter.end, `is`(11951309L))
    assertThat(firstChapter.endTime, `is`("271.004739"))
    assertThat(firstChapter.tags?.title, `is`("01 - Sammy Jay Makes a Fuss"))

    val lastChapter = info.chapters!![info.chapters!!.size - 1]
    assertThat(lastChapter.timeBase, `is`("1/44100"))
    assertThat(lastChapter.start, `is`(237875790L))
    assertThat(lastChapter.startTime, `is`("5394.008844"))
    assertThat(lastChapter.end, `is`(248628224L))
    assertThat(lastChapter.endTime, `is`("5637.828209"))
    assertThat(lastChapter.tags?.title, `is`("24 - Chatterer Has His Turn to Laugh"))
  }

  @Test
  fun testProbeWithPackets() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_packets)
        .setShowPackets(true)
        .build()
    )
    assertThat(info.hasError(), `is`(false))
    assertThat(info.packets!!.size, `is`(381))

    val firstPacket = info.packets!![0]
    assertThat(firstPacket.codecType, `is`(CodecType.Audio))
    assertThat(firstPacket.streamIndex, `is`(1))
    assertThat(firstPacket.pts, `is`(0L))
    assertThat(firstPacket.ptsTime, `is`("0.000000"))
    assertThat(firstPacket.dts, `is`(0L))
    assertThat(firstPacket.dtsTime, `is`("0.000000"))
    assertThat(firstPacket.duration, `is`(1024L))
    assertThat(firstPacket.durationTime, `is`("0.021333"))
    assertThat(firstPacket.size, `is`("967"))
    assertThat(firstPacket.pos, `is`("4261"))
    assertThat(firstPacket.flags, `is`("K_"))

    val secondPacket = info.packets!![1]
    assertThat(secondPacket.codecType, `is`(CodecType.Video))
    assertThat(secondPacket.streamIndex, `is`(0))
    assertThat(secondPacket.pts, `is`(0L))
    assertThat(secondPacket.ptsTime, `is`("0.000000"))
    assertThat(secondPacket.dts, `is`(0L))
    assertThat(secondPacket.dtsTime, `is`("0.000000"))
    assertThat(secondPacket.duration, `is`(512L))
    assertThat(secondPacket.durationTime, `is`("0.040000"))
    assertThat(secondPacket.size, `is`("105222"))
    assertThat(secondPacket.pos, `is`("5228"))
    assertThat(secondPacket.flags, `is`("K_"))

    val lastPacket = info.packets!![info.packets!!.size - 1]
    assertThat(lastPacket.codecType, `is`(CodecType.Audio))
    assertThat(lastPacket.streamIndex, `is`(1))
    assertThat(lastPacket.pts, `is`(253952L))
    assertThat(lastPacket.ptsTime, `is`("5.290667"))
    assertThat(lastPacket.dts, `is`(253952L))
    assertThat(lastPacket.dtsTime, `is`("5.290667"))
    assertThat(lastPacket.duration, `is`(1024L))
    assertThat(lastPacket.durationTime, `is`("0.021333"))
    assertThat(lastPacket.size, `is`("1111"))
    assertThat(lastPacket.pos, `is`("1054609"))
    assertThat(lastPacket.flags, `is`("K_"))
  }

  @Test
  fun testProbeWithFrames() {
    val info = ffprobe.probe(
      ffprobe.builder()
        .setInput(Samples.big_buck_bunny_720p_1mb_with_frames)
        .setShowFrames(true)
        .build()
    )
    assertThat(info.hasError(), `is`(false))
    assertThat(info.frames!!.size, `is`(381))

    val firstFrame = info.frames!![0]
    assertThat(firstFrame.streamIndex, `is`(1))
    assertThat(firstFrame.keyFrame, `is`(1))
    assertThat(firstFrame.pktPts, `is`(0L))
    assertThat(firstFrame.pktPtsTime, `is`("0.000000"))
    assertThat(firstFrame.pktDts, `is`(0L))
    assertThat(firstFrame.pktDtsTime, `is`("0.000000"))
    assertThat(firstFrame.bestEffortTimestamp, `is`(0L))
    assertThat(firstFrame.bestEffortTimestampTime, `is`("0.000000"))
    assertThat(firstFrame.pktDuration, `is`(1024L))
    assertThat(firstFrame.pktDurationTime, `is`("0.021333"))
    assertThat(firstFrame.pktPos, `is`(4261L))
    assertThat(firstFrame.pktSize, `is`(967L))
    assertThat(firstFrame.sampleFmt, `is`("fltp"))
    assertThat(firstFrame.nbSamples, `is`(1024))
    assertThat(firstFrame.channels, `is`(6))
    assertThat(firstFrame.channelLayout, `is`("5.1"))

    val secondFrame = info.frames!![1]
    assertThat(secondFrame.mediaType, `is`(CodecType.Video))
    assertThat(secondFrame.streamIndex, `is`(0))
    assertThat(secondFrame.keyFrame, `is`(1))
    assertThat(secondFrame.pktPts, `is`(0L))
    assertThat(secondFrame.pktPtsTime, `is`("0.000000"))
    assertThat(secondFrame.pktDts, `is`(0L))
    assertThat(secondFrame.pktDtsTime, `is`("0.000000"))
    assertThat(secondFrame.bestEffortTimestamp, `is`(0L))
    assertThat(secondFrame.bestEffortTimestampTime, `is`("0.000000"))
    assertThat(secondFrame.pktDuration, `is`(512L))
    assertThat(secondFrame.pktDurationTime, `is`("0.040000"))
    assertThat(secondFrame.pktPos, `is`(5228L))
    assertThat(secondFrame.pktSize, `is`(105222L))
    assertThat(secondFrame.sampleFmt, IsNull())
    assertThat(secondFrame.nbSamples, `is`(0))
    assertThat(secondFrame.channels, `is`(0))
    assertThat(secondFrame.channelLayout, IsNull())

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
        .build()
    )
    assertThat(info.hasError(), `is`(false))
    assertThat(info.packets!!.size, `is`(381))
    assertThat(info.frames!!.size, `is`(381))

    val firstPacket = info.packets!![0]
    assertThat(firstPacket.codecType, `is`(CodecType.Audio))
    assertThat(firstPacket.streamIndex, `is`(1))
    assertThat(firstPacket.pts, `is`(0L))
    assertThat(firstPacket.ptsTime, `is`("0.000000"))
    assertThat(firstPacket.dts, `is`(0L))
    assertThat(firstPacket.dtsTime, `is`("0.000000"))
    assertThat(firstPacket.duration, `is`(1024L))
    assertThat(firstPacket.durationTime, `is`("0.021333"))
    assertThat(firstPacket.size, `is`("967"))
    assertThat(firstPacket.pos, `is`("4261"))
    assertThat(firstPacket.flags, `is`("K_"))

    val secondPacket = info.packets!![1]
    assertThat(secondPacket.codecType, `is`(CodecType.Video))
    assertThat(secondPacket.streamIndex, `is`(0))
    assertThat(secondPacket.pts, `is`(0L))
    assertThat(secondPacket.ptsTime, `is`("0.000000"))
    assertThat(secondPacket.dts, `is`(0L))
    assertThat(secondPacket.dtsTime, `is`("0.000000"))
    assertThat(secondPacket.duration, `is`(512L))
    assertThat(secondPacket.durationTime, `is`("0.040000"))
    assertThat(secondPacket.size, `is`("105222"))
    assertThat(secondPacket.pos, `is`("5228"))
    assertThat(secondPacket.flags, `is`("K_"))

    val lastPacket = info.packets!![info.packets!!.size - 1]
    assertThat(lastPacket.codecType, `is`(CodecType.Audio))
    assertThat(lastPacket.streamIndex, `is`(1))
    assertThat(lastPacket.pts, `is`(253952L))
    assertThat(lastPacket.ptsTime, `is`("5.290667"))
    assertThat(lastPacket.dts, `is`(253952L))
    assertThat(lastPacket.dtsTime, `is`("5.290667"))
    assertThat(lastPacket.duration, `is`(1024L))
    assertThat(lastPacket.durationTime, `is`("0.021333"))
    assertThat(lastPacket.size, `is`("1111"))
    assertThat(lastPacket.pos, `is`("1054609"))
    assertThat(lastPacket.flags, `is`("K_"))

    val firstFrame = info.frames!![0]
    assertThat(firstFrame.streamIndex, `is`(1))
    assertThat(firstFrame.keyFrame, `is`(1))
    assertThat(firstFrame.pktPts, `is`(0L))
    assertThat(firstFrame.pktPtsTime, `is`("0.000000"))
    assertThat(firstFrame.pktDts, `is`(0L))
    assertThat(firstFrame.pktDtsTime, `is`("0.000000"))
    assertThat(firstFrame.bestEffortTimestamp, `is`(0L))
    assertThat(firstFrame.bestEffortTimestampTime, `is`("0.000000"))
    assertThat(firstFrame.pktDuration, `is`(1024L))
    assertThat(firstFrame.pktDurationTime, `is`("0.021333"))
    assertThat(firstFrame.pktPos, `is`(4261L))
    assertThat(firstFrame.pktSize, `is`(967L))
    assertThat(firstFrame.sampleFmt, `is`("fltp"))
    assertThat(firstFrame.nbSamples, `is`(1024))
    assertThat(firstFrame.channels, `is`(6))
    assertThat(firstFrame.channelLayout, `is`("5.1"))

    val secondFrame = info.frames!![1]
    assertThat(secondFrame.mediaType, `is`(CodecType.Video))
    assertThat(secondFrame.streamIndex, `is`(0))
    assertThat(secondFrame.keyFrame, `is`(1))
    assertThat(secondFrame.pktPts, `is`(0L))
    assertThat(secondFrame.pktPtsTime, `is`("0.000000"))
    assertThat(secondFrame.pktDts, `is`(0L))
    assertThat(secondFrame.pktDtsTime, `is`("0.000000"))
    assertThat(secondFrame.bestEffortTimestamp, `is`(0L))
    assertThat(secondFrame.bestEffortTimestampTime, `is`("0.000000"))
    assertThat(secondFrame.pktDuration, `is`(512L))
    assertThat(secondFrame.pktDurationTime, `is`("0.040000"))
    assertThat(secondFrame.pktPos, `is`(5228L))
    assertThat(secondFrame.pktSize, `is`(105222L))
    assertThat(secondFrame.sampleFmt, IsNull())
    assertThat(secondFrame.nbSamples, `is`(0))
    assertThat(secondFrame.channels, `is`(0))
    assertThat(secondFrame.channelLayout, IsNull())

    val lastFrame = info.frames!![info.frames!!.size - 1]
    assertLastFrame(lastFrame)
  }

  private fun assertLastFrame(actual: FFmpegFrame) {
    assertThat(actual.mediaType, `is`(CodecType.Audio))
    assertThat(actual.streamIndex, `is`(1))
    assertThat(actual.keyFrame, `is`(1))
    assertThat(actual.pktPts, `is`(253952L))
    assertThat(actual.pktPtsTime, `is`("5.290667"))
    assertThat(actual.pktDts, `is`(253952L))
    assertThat(actual.pktDtsTime, `is`("5.290667"))
    assertThat(actual.bestEffortTimestamp, `is`(253952L))
    assertThat(actual.bestEffortTimestampTime, `is`("5.290667"))
    assertThat(actual.pktDuration, `is`(1024L))
    assertThat(actual.pktDurationTime, `is`("0.021333"))
    assertThat(actual.pktPos, `is`(1054609L))
    assertThat(actual.pktSize, `is`(1111L))
    assertThat(actual.sampleFmt, `is`("fltp"))
    assertThat(actual.nbSamples, `is`(1024))
    assertThat(actual.channels, `is`(6))
    assertThat(actual.channelLayout, `is`("5.1"))
  }

  @Test
  fun testProbeVideo2() {
    val info = ffprobe.probe(Samples.always_on_my_mind)
    assertFalse(info.hasError())

    assertThat(info.streams, hasSize(2))
    assertThat(info.streams!![0].codecType, `is`(CodecType.Video))
    assertThat(info.streams!![1].codecType, `is`(CodecType.Audio))

    assertThat(info.streams!![1].channels, `is`(2))
    assertThat(info.streams!![1].sampleRate, `is`(48_000))

    assertThat(
      info.format!!.filename,
      `is`("c:\\Users\\Bob\\Always On My Mind [Program Only] - Adelén.mp4")
    )
  }

  @Test
  fun testProbeStartPts() {
    val info = ffprobe.probe(Samples.start_pts_test)
    assertFalse(info.hasError())

    assertThat(info.streams!![0].startPts, `is`(8570867078L))
  }

  @Test
  fun testProbeDivideByZero() {
    val info = ffprobe.probe(Samples.divide_by_zero)
    assertFalse(info.hasError())

    assertThat(info.streams!![1].codecTimeBase, `is`(Fraction.ZERO))
  }

  @Test
  fun shouldThrowOnErrorWithFFmpegProbeResult() {
    every { mockProcess.exitValue() } answers { 1 }

    val error = FFmpegError()
    val result = FFmpegProbeResult(error)

    val exception = assertThrows(FFmpegException::class.java) {
      ffprobe.throwOnError(mockProcess, result)
    }
    assertEquals(error, exception.error)
  }

  @Test
  fun shouldThrowOnErrorEvenIfProbeResultHasNoError() {
    every { mockProcess.exitValue() } answers { 1 }

    val result = FFmpegProbeResult()
    val exception = assertThrows(FFmpegException::class.java) {
      ffprobe.throwOnError(mockProcess, result)
    }
    assertNull(exception.error)
  }

  @Test
  fun shouldThrowOnErrorEvenIfProbeResultIsNull() {
    every { mockProcess.exitValue() } answers { 1 }

    val exception = assertThrows(FFmpegException::class.java) {
      ffprobe.throwOnError(mockProcess, null)
    }
    assertNull(exception.error)
  }

  @Test
  fun testShouldThrowErrorWithoutMock() {
    val probe = FFprobe(FFprobe.DEFAULT_PATH)
    val exception = assertThrows(FFmpegException::class.java) {
      probe.probe("doesnotexist.mp4")
    }

    assertNotNull(exception)
    assertNotNull(exception.error)
    assertNotNull(exception.error!!.string)
    assertNotEquals(0, exception.error!!.code)
  }

  @Test
  fun testProbeSideDataList() {
    val info = ffprobe.probe(Samples.side_data_list)

    assertThat(info.streams!![0].sideDataList.size, `is`(1))
    assertThat(info.streams!![0].sideDataList[0].sideDataType, `is`("Display Matrix"))
    assertThat(
      info.streams!![0].sideDataList[0].displayMatrix,
      `is`("\n00000000:            0      -65536           0\n00000001:        65536           0           0\n00000002:            0           0  1073741824\n")
    )
    assertThat(info.streams!![0].sideDataList[0].rotation, `is`(90))
  }

  @Test
  fun testChaptersWithLongIds() {
    val info = ffprobe.probe(Samples.chapters_with_long_id)

    assertThat(info.chapters!![0].id, `is`(6613449456311024506L))
    assertThat(info.chapters!![1].id, `is`(-4433436293284298339L))
  }

  @Test
  fun testProbeDefaultArguments() {
    ffprobe.probe(Samples.always_on_my_mind)

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    assertThat(
      value,
      `is`(
        listOf(
          "-v", "quiet",
          "-print_format", "json",
          "-show_error",
          "-show_format",
          "-show_streams",
          "-show_chapters",
          Samples.always_on_my_mind
        )
      )
    )
  }

  @Test
  fun testProbeProbeBuilder() {
    ffprobe.probe(FFprobeBuilder().setInput(Samples.always_on_my_mind))

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    assertThat(
      value,
      `is`(
        listOf(
          "-v", "quiet",
          "-print_format", "json",
          "-show_error",
          "-show_format",
          "-show_streams",
          "-show_chapters",
          Samples.always_on_my_mind
        )
      )
    )
  }

  @Test
  fun testProbeProbeBuilderBuilt() {
    ffprobe.probe(FFprobeBuilder().setInput(Samples.always_on_my_mind).build())

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    assertThat(
      value,
      `is`(
        listOf(
          "-v", "quiet",
          "-print_format", "json",
          "-show_error",
          "-show_format",
          "-show_streams",
          "-show_chapters",
          Samples.always_on_my_mind
        )
      )
    )
  }

  @Test
  fun testProbeProbeExtraArgs() {
    ffprobe.probe(Samples.always_on_my_mind, null, "-rw_timeout", "0")

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    assertThat(
      value,
      `is`(
        listOf(
          "-v", "quiet",
          "-print_format", "json",
          "-show_error",
          "-rw_timeout", "0",
          "-show_format",
          "-show_streams",
          "-show_chapters",
          Samples.always_on_my_mind
        )
      )
    )
  }

  @Test
  fun testProbeProbeUserAgent() {
    ffprobe.probe(Samples.always_on_my_mind, "ffmpeg-cli-wrapper")

    val slot = slot<List<String>>()
    verify { runFunc.run(capture(slot)) }

    val value = Helper.subList(slot.captured, 1)

    assertThat(
      value,
      `is`(
        listOf(
          "-v", "quiet",
          "-print_format", "json",
          "-show_error",
          "-user_agent", "ffmpeg-cli-wrapper",
          "-show_format",
          "-show_streams",
          "-show_chapters",
          Samples.always_on_my_mind
        )
      )
    )
  }

  @Test
  fun testFullFormatDeserialization() {
    val format = ffprobe.probe(Samples.always_on_my_mind).format!!

    assertThat(format.filename, endsWith("Always On My Mind [Program Only] - Adelén.mp4"))
    assertEquals(2, format.nbStreams)
    assertEquals(0, format.nbPrograms)
    assertEquals("mov,mp4,m4a,3gp,3g2,mj2", format.formatName)
    assertEquals("QuickTime / MOV", format.formatLongName)
    assertEquals(0.0, format.startTime ?: 0.0, 0.01)
    assertEquals(181.632, format.duration ?: 0.0, 0.01)
    assertEquals(417127573, format.size)
    assertEquals(18372426, format.bitRate)
    assertEquals(100, format.probeScore)
    assertEquals(4, format.tags?.size)

    assertEquals("mp42", format.tags!!["major_brand"])
  }

  @Test
  fun testFullChaptersDeserialization() {
    val chapters = ffprobe.probe(Samples.book_with_chapters).chapters!!
    val chapter = chapters[chapters.size - 1]

    assertEquals(24, chapters.size)

    assertEquals(23, chapter.id)
    assertEquals("1/44100", chapter.timeBase)
    assertEquals(237875790, chapter.start)
    assertEquals("5394.008844", chapter.startTime)
    assertEquals(248628224, chapter.end)
    assertEquals("5637.828209", chapter.endTime)
    assertEquals("24 - Chatterer Has His Turn to Laugh", chapter.tags?.title)
  }

  @Test
  fun testFullVideoStreamDeserialization() {
    val streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).streams!!
    val stream = streams[0]

    assertEquals(0, stream.index)
    assertEquals("h264", stream.codecName)
    assertEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", stream.codecLongName)
    assertEquals("Main", stream.profile)
    assertEquals(CodecType.Video, stream.codecType)
    assertEquals(Fraction.getFraction(1, 50), stream.codecTimeBase)
    assertEquals("avc1", stream.codecTagString)
    assertEquals("0x31637661", stream.codecTag)
    assertEquals(1280, stream.width)
    assertEquals(720, stream.height)
    assertEquals(0, stream.hasBFrames)
    assertEquals("1:1", stream.sampleAspectRatio)
    assertEquals("16:9", stream.displayAspectRatio)
    assertEquals("yuv420p", stream.pixFmt)
    assertEquals(31, stream.level)
    assertEquals("left", stream.chromaLocation)
    assertEquals(1, stream.refs)
    assertEquals("true", stream.isAvc)
    assertEquals("4", stream.nalLengthSize)
    assertEquals("0x1", stream.id)
    assertEquals(Fraction.getFraction(25, 1), stream.rFrameRate)
    assertEquals(Fraction.getFraction(25, 1), stream.avgFrameRate)
    assertEquals(Fraction.getFraction(1, 12800), stream.timeBase)
    assertEquals(0, stream.startPts)
    assertEquals(0.0, stream.startTime ?: 0.0, 0.01)
    assertEquals(67584, stream.durationTs)
    assertEquals(5.28, stream.duration ?: 0.0, 0.01)
    assertEquals(1205959, stream.bitRate)
    assertEquals(0, stream.maxBitRate)
    assertEquals(8, stream.bitsPerRawSample)
    assertEquals(0, stream.bitsPerSample)
    assertEquals(132, stream.nbFrames)
    assertNull(stream.sampleFmt)
    assertEquals(0, stream.sampleRate)
    assertEquals(0, stream.channels)
    assertNull(stream.channelLayout)
    assertEquals(4, stream.tags?.size)
    assertEquals("und", stream.tags!!["language"])
    assertEquals(0, stream.sideDataList.size)
  }

  @Test
  fun testFullAudioStreamDeserialization() {
    val streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).streams!!
    val stream = streams[1]

    assertEquals(1, stream.index)
    assertEquals("aac", stream.codecName)
    assertEquals("AAC (Advanced Audio Coding)", stream.codecLongName)
    assertEquals("LC", stream.profile)
    assertEquals(CodecType.Audio, stream.codecType)
    assertEquals(Fraction.getFraction(1, 48_000), stream.codecTimeBase)
    assertEquals("mp4a", stream.codecTagString)
    assertEquals("0x6134706d", stream.codecTag)
    assertEquals(0, stream.width)
    assertEquals(0, stream.height)
    assertEquals(0, stream.hasBFrames)
    assertNull(stream.sampleAspectRatio)
    assertNull(stream.displayAspectRatio)
    assertNull(stream.pixFmt)
    assertEquals(0, stream.level)
    assertNull(stream.chromaLocation)
    assertEquals(0, stream.refs)
    assertNull(stream.isAvc)
    assertNull(stream.nalLengthSize)
    assertEquals("0x2", stream.id)
    assertEquals(Fraction.getFraction(0, 1), stream.rFrameRate)
    assertEquals(Fraction.getFraction(0, 1), stream.avgFrameRate)
    assertEquals(Fraction.getFraction(1, 48_000), stream.timeBase)
    assertEquals(0, stream.startPts)
    assertEquals(0.0, stream.startTime ?: 0.0, 0.01)
    assertEquals(254976, stream.durationTs)
    assertEquals(5.312, stream.duration ?: 0.0, 0.01)
    assertEquals(384828, stream.bitRate)
    assertEquals(400392, stream.maxBitRate)
    assertEquals(0, stream.bitsPerRawSample)
    assertEquals(0, stream.bitsPerSample)
    assertEquals(249, stream.nbFrames)
    assertEquals("fltp", stream.sampleFmt)
    assertEquals(48000, stream.sampleRate)
    assertEquals(6, stream.channels)
    assertEquals("5.1", stream.channelLayout)
    assertEquals(4, stream.tags?.size)
    assertEquals("und", stream.tags!!["language"])
    assertEquals(0, stream.sideDataList.size)
  }

  @Test
  fun testSideDataListDeserialization() {
    val streams = ffprobe.probe(Samples.side_data_list).streams!!
    val sideDataList = streams[0].sideDataList

    assertEquals(1, sideDataList.size)
    assertEquals("Display Matrix", sideDataList[0].sideDataType)
    assertEquals(90, sideDataList[0].rotation)
    assertThat(sideDataList[0].displayMatrix, startsWith("\n00000000:"))
  }

  @Test
  fun testDispositionDeserialization() {
    val streams = ffprobe.probe(Samples.side_data_list).streams!!
    val disposition = streams[0].disposition!!

    assertTrue(disposition.isDefault())
    assertFalse(disposition.isDub())
    assertFalse(disposition.isOriginal())
    assertFalse(disposition.isComment())
    assertFalse(disposition.isLyrics())
    assertFalse(disposition.isKaraoke())
    assertFalse(disposition.isForced())
    assertFalse(disposition.isHearingImpaired())
    assertFalse(disposition.isVisualImpaired())
    assertFalse(disposition.isCleanEffects())
    assertFalse(disposition.isAttachedPic())
    assertFalse(disposition.isCaptions())
    assertFalse(disposition.isDescriptions())
    assertFalse(disposition.isMetadata())
  }

  @Ignore("Broken until we fix mocking in Kotlin")
  @Test
  fun testDispositionWithAllFieldsTrueDeserialization() {
    val streams = ffprobe.probe(Samples.disposition_all_true).streams!!
    val disposition = streams[0].disposition!!

    assertTrue(disposition.isDefault())
    assertTrue(disposition.isDub())
    assertTrue(disposition.isOriginal())
    assertTrue(disposition.isComment())
    assertTrue(disposition.isLyrics())
    assertTrue(disposition.isKaraoke())
    assertTrue(disposition.isForced())
    assertTrue(disposition.isHearingImpaired())
    assertTrue(disposition.isVisualImpaired())
    assertTrue(disposition.isCleanEffects())
    assertTrue(disposition.isAttachedPic())
    assertTrue(disposition.isTimedThumbnails())
    assertTrue(disposition.isNonDiegetic())
    assertTrue(disposition.isCaptions())
    assertTrue(disposition.isDescriptions())
    assertTrue(disposition.isMetadata())
    assertTrue(disposition.isDependent())
    assertTrue(disposition.isStillImage())
  }

  @Test
  fun testFullPacketDeserialization() {
    val probeBuilder = ffprobe.builder()
      .setShowPackets(true)
      .setInput(Samples.big_buck_bunny_720p_1mb_with_packets)
    val packets = ffprobe.probe(probeBuilder).packets!!

    val packet = packets[packets.size - 1]

    assertEquals(CodecType.Audio, packet.codecType)
    assertEquals(1, packet.streamIndex)
    assertEquals(253952, packet.pts)
    assertEquals("5.290667", packet.ptsTime)
    assertEquals(253952, packet.dts)
    assertEquals("5.290667", packet.dtsTime)
    assertEquals(1024, packet.duration)
    assertEquals("0.021333", packet.durationTime)
    assertEquals("1111", packet.size)
    assertEquals("1054609", packet.pos)
    assertEquals("K_", packet.flags)
  }

  @Test
  fun testFullFrameDeserialization() {
    val probeBuilder = ffprobe.builder()
      .setShowFrames(true)
      .setInput(Samples.big_buck_bunny_720p_1mb_with_frames)
    val frames = ffprobe.probe(probeBuilder).frames!!

    val frame = frames[frames.size - 1]

    assertEquals(CodecType.Audio, frame.mediaType)
    assertEquals(1, frame.streamIndex)
    assertEquals(1, frame.keyFrame)
    assertEquals(253952, frame.pktPts)
    assertEquals("5.290667", frame.pktPtsTime)
    assertEquals(253952, frame.pktDts)
    assertEquals("5.290667", frame.pktDtsTime)
    assertEquals(253952, frame.bestEffortTimestamp)
    assertEquals("5.290667", frame.bestEffortTimestampTime)
    assertEquals(1024, frame.pktDuration)
    assertEquals("0.021333", frame.pktDurationTime)
    assertEquals(1054609, frame.pktPos)
    assertEquals(1111, frame.pktSize)
    assertEquals("fltp", frame.sampleFmt)
    assertEquals(1024, frame.nbSamples)
    assertEquals(6, frame.channels)
    assertEquals("5.1", frame.channelLayout)
  }
}
