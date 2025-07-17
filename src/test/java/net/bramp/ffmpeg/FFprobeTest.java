package net.bramp.ffmpeg;

import static net.bramp.ffmpeg.FFmpegTest.argThatHasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import net.bramp.ffmpeg.builder.FFprobeBuilder;
import net.bramp.ffmpeg.fixtures.Samples;
import net.bramp.ffmpeg.lang.NewProcessAnswer;
import net.bramp.ffmpeg.probe.*;
import net.bramp.ffmpeg.shared.CodecType;
import org.apache.commons.lang3.math.Fraction;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FFprobeTest {

  @Mock ProcessFunction runFunc;
  @Mock Process mockProcess;

  @Captor ArgumentCaptor<List<String>> argsCaptor;

  FFprobe ffprobe;

  @Before
  public void before() throws IOException {
    when(runFunc.run(argThatHasItem("-version")))
        .thenAnswer(new NewProcessAnswer("ffprobe-version"));

    when(runFunc.run(argThatHasItem(Samples.big_buck_bunny_720p_1mb)))
        .thenAnswer(new NewProcessAnswer("ffprobe-big_buck_bunny_720p_1mb.mp4"));

    when(runFunc.run(argThatHasItem(Samples.always_on_my_mind)))
        .thenAnswer(new NewProcessAnswer("ffprobe-Always On My Mind [Program Only] - Adelen.mp4"));

    when(runFunc.run(argThatHasItem(Samples.start_pts_test)))
        .thenAnswer(new NewProcessAnswer("ffprobe-start_pts_test"));

    when(runFunc.run(argThatHasItem(Samples.divide_by_zero)))
        .thenAnswer(new NewProcessAnswer("ffprobe-divide-by-zero"));

    when(runFunc.run(argThatHasItem(Samples.book_with_chapters)))
        .thenAnswer(new NewProcessAnswer("book_with_chapters.m4b"));

    when(runFunc.run(argThatHasItem(Samples.big_buck_bunny_720p_1mb_with_packets)))
        .thenAnswer(new NewProcessAnswer("ffprobe-big_buck_bunny_720p_1mb_packets.mp4"));

    when(runFunc.run(argThatHasItem(Samples.big_buck_bunny_720p_1mb_with_frames)))
        .thenAnswer(new NewProcessAnswer("ffprobe-big_buck_bunny_720p_1mb_frames.mp4"));

    when(runFunc.run(argThatHasItem(Samples.big_buck_bunny_720p_1mb_with_packets_and_frames)))
        .thenAnswer(new NewProcessAnswer("ffprobe-big_buck_bunny_720p_1mb_packets_and_frames.mp4"));

    when(runFunc.run(argThatHasItem(Samples.side_data_list)))
        .thenAnswer(new NewProcessAnswer("ffprobe-side_data_list"));

//    when(runFunc.run(argThatHasItem(Samples.disposition_all_true)))
//        .thenAnswer(new NewProcessAnswer("ffprobe-disposition_all_true"));

    when(runFunc.run(argThatHasItem(Samples.chapters_with_long_id)))
        .thenAnswer(new NewProcessAnswer("chapters_with_long_id.m4b"));

    ffprobe = new FFprobe(runFunc);
  }

  @Test
  public void testVersion() throws Exception {
    assertEquals(
        "ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers", ffprobe.version());
    assertEquals(
        "ffprobe version 3.0.2 Copyright (c) 2007-2016 the FFmpeg developers", ffprobe.version());

    verify(runFunc, times(1)).run(argThatHasItem("-version"));
  }

  @Test
  public void testProbeVideo() throws IOException {
    FFmpegProbeResult info = ffprobe.probe(Samples.big_buck_bunny_720p_1mb);
    assertFalse(info.hasError());

    // Only a quick sanity check until we do something better
    assertThat(info.getStreams(), hasSize(2));
    assertThat(info.getStreams().get(0).getCodec_type(), is(CodecType.VIDEO));
    assertThat(info.getStreams().get(1).getCodec_type(), is(CodecType.AUDIO));

    assertThat(info.getStreams().get(1).getChannels(), is(6));
    assertThat(info.getStreams().get(1).getSample_rate(), is(48_000));

    assertThat(info.getChapters().isEmpty(), is(true));
    // System.out.println(FFmpegUtils.getGson().toJson(info));
  }

  @Test
  public void testProbeBookWithChapters() throws IOException {
    FFmpegProbeResult info = ffprobe.probe(Samples.book_with_chapters);
    assertThat(info.hasError(), is(false));
    assertThat(info.getChapters().size(), is(24));

    FFmpegChapter firstChapter = info.getChapters().get(0);
    assertThat(firstChapter.getTime_base(), is("1/44100"));
    assertThat(firstChapter.getStart(), is(0L));
    assertThat(firstChapter.getStart_time(), is("0.000000"));
    assertThat(firstChapter.getEnd(), is(11951309L));
    assertThat(firstChapter.getEnd_time(), is("271.004739"));
    assertThat(firstChapter.getTags().getTitle(), is("01 - Sammy Jay Makes a Fuss"));

    FFmpegChapter lastChapter = info.getChapters().get(info.getChapters().size() - 1);
    assertThat(lastChapter.getTime_base(), is("1/44100"));
    assertThat(lastChapter.getStart(), is(237875790L));
    assertThat(lastChapter.getStart_time(), is("5394.008844"));
    assertThat(lastChapter.getEnd(), is(248628224L));
    assertThat(lastChapter.getEnd_time(), is("5637.828209"));
    assertThat(lastChapter.getTags().getTitle(), is("24 - Chatterer Has His Turn to Laugh"));
  }

  @Test
  public void testProbeWithPackets() throws IOException {
    FFmpegProbeResult info =
        ffprobe.probe(
            ffprobe
                .builder()
                .setInput(Samples.big_buck_bunny_720p_1mb_with_packets)
                .setShowPackets(true)
                .build());
    assertThat(info.hasError(), is(false));
    assertThat(info.getPackets().size(), is(381));

    FFmpegPacket firstPacket = info.getPackets().get(0);
    assertThat(firstPacket.getCodec_type(), is(CodecType.AUDIO));
    assertThat(firstPacket.getStream_index(), is(1));
    assertThat(firstPacket.getPts(), is(0L));
    assertThat(firstPacket.getPts_time(), is(0.0));
    assertThat(firstPacket.getDts(), is(0L));
    assertThat(firstPacket.getDts_time(), is(0.0));
    assertThat(firstPacket.getDuration(), is(1024L));
    assertThat(firstPacket.getDuration_time(), is(0.021333F));
    assertThat(firstPacket.getSize(), is("967"));
    assertThat(firstPacket.getPos(), is("4261"));
    assertThat(firstPacket.getFlags(), is("K_"));

    FFmpegPacket secondPacket = info.getPackets().get(1);
    assertThat(secondPacket.getCodec_type(), is(CodecType.VIDEO));
    assertThat(secondPacket.getStream_index(), is(0));
    assertThat(secondPacket.getPts(), is(0L));
    assertThat(secondPacket.getPts_time(), is(0.0));
    assertThat(secondPacket.getDts(), is(0L));
    assertThat(secondPacket.getDts_time(), is(0.0));
    assertThat(secondPacket.getDuration(), is(512L));
    assertThat(secondPacket.getDuration_time(), is(0.04F));
    assertThat(secondPacket.getSize(), is("105222"));
    assertThat(secondPacket.getPos(), is("5228"));
    assertThat(secondPacket.getFlags(), is("K_"));

    FFmpegPacket lastPacket = info.getPackets().get(info.getPackets().size() - 1);
    assertThat(lastPacket.getCodec_type(), is(CodecType.AUDIO));
    assertThat(lastPacket.getStream_index(), is(1));
    assertThat(lastPacket.getPts(), is(253952L));
    assertThat(lastPacket.getPts_time(), is(5.290667));
    assertThat(lastPacket.getDts(), is(253952L));
    assertThat(lastPacket.getDts_time(), is(5.290667));
    assertThat(lastPacket.getDuration(), is(1024L));
    assertThat(lastPacket.getDuration_time(), is(0.021333F));
    assertThat(lastPacket.getSize(), is("1111"));
    assertThat(lastPacket.getPos(), is("1054609"));
    assertThat(lastPacket.getFlags(), is("K_"));
  }

  @Test
  public void testProbeWithFrames() throws IOException {
    FFmpegProbeResult info =
        ffprobe.probe(
            ffprobe
                .builder()
                .setInput(Samples.big_buck_bunny_720p_1mb_with_frames)
                .setShowFrames(true)
                .build());
    assertThat(info.hasError(), is(false));
    assertThat(info.getFrames().size(), is(381));

    FFmpegFrame firstFrame = info.getFrames().get(0);
    assertThat(firstFrame.getStream_index(), is(1));
    assertThat(firstFrame.getKey_frame(), is(1));
    assertThat(firstFrame.getPkt_pts(), is(0L));
    assertThat(firstFrame.getPkt_pts_time(), is(0.0));
    assertThat(firstFrame.getPkt_dts(), is(0L));
    assertThat(firstFrame.getPkt_dts_time(), is(0.0));
    assertThat(firstFrame.getBest_effort_timestamp(), is(0L));
    assertThat(firstFrame.getBest_effort_timestamp_time(), is(0.0F));
    assertThat(firstFrame.getPkt_duration(), is(1024L));
    assertThat(firstFrame.getPkt_duration_time(), is(0.021333F));
    assertThat(firstFrame.getPkt_pos(), is(4261L));
    assertThat(firstFrame.getPkt_size(), is(967L));
    assertThat(firstFrame.getSample_fmt(), is("fltp"));
    assertThat(firstFrame.getNb_samples(), is(1024));
    assertThat(firstFrame.getChannels(), is(6));
    assertThat(firstFrame.getChannel_layout(), is("5.1"));

    FFmpegFrame secondFrame = info.getFrames().get(1);
    assertThat(secondFrame.getMedia_type(), is(CodecType.VIDEO));
    assertThat(secondFrame.getStream_index(), is(0));
    assertThat(secondFrame.getKey_frame(), is(1));
    assertThat(secondFrame.getPkt_pts(), is(0L));
    assertThat(secondFrame.getPkt_pts_time(), is(0.0));
    assertThat(secondFrame.getPkt_dts(), is(0L));
    assertThat(secondFrame.getPkt_dts_time(), is(0.0));
    assertThat(secondFrame.getBest_effort_timestamp(), is(0L));
    assertThat(secondFrame.getBest_effort_timestamp_time(), is(0.0F));
    assertThat(secondFrame.getPkt_duration(), is(512L));
    assertThat(secondFrame.getPkt_duration_time(), is(0.04F));
    assertThat(secondFrame.getPkt_pos(), is(5228L));
    assertThat(secondFrame.getPkt_size(), is(105222L));
    assertThat(secondFrame.getSample_fmt(), new IsNull<>());
    assertThat(secondFrame.getNb_samples(), is(0));
    assertThat(secondFrame.getChannels(), is(0));
    assertThat(secondFrame.getChannel_layout(), new IsNull<>());

    FFmpegFrame lastFrame = info.getFrames().get(info.getFrames().size() - 1);
    assertLastFrame(lastFrame);
  }

  @Test
  public void testProbeWithPacketsAndFrames() throws IOException {
    FFmpegProbeResult info =
        ffprobe.probe(
            ffprobe
                .builder()
                .setInput(Samples.big_buck_bunny_720p_1mb_with_packets_and_frames)
                .setShowPackets(true)
                .setShowFrames(true)
                .build());
    assertThat(info.hasError(), is(false));
    assertThat(info.getPackets().size(), is(381));
    assertThat(info.getFrames().size(), is(381));

    FFmpegPacket firstPacket = info.getPackets().get(0);
    assertThat(firstPacket.getCodec_type(), is(CodecType.AUDIO));
    assertThat(firstPacket.getStream_index(), is(1));
    assertThat(firstPacket.getPts(), is(0L));
    assertThat(firstPacket.getPts_time(), is(0.0));
    assertThat(firstPacket.getDts(), is(0L));
    assertThat(firstPacket.getDts_time(), is(0.0));
    assertThat(firstPacket.getDuration(), is(1024L));
    assertThat(firstPacket.getDuration_time(), is(0.021333F));
    assertThat(firstPacket.getSize(), is("967"));
    assertThat(firstPacket.getPos(), is("4261"));
    assertThat(firstPacket.getFlags(), is("K_"));

    FFmpegPacket secondPacket = info.getPackets().get(1);
    assertThat(secondPacket.getCodec_type(), is(CodecType.VIDEO));
    assertThat(secondPacket.getStream_index(), is(0));
    assertThat(secondPacket.getPts(), is(0L));
    assertThat(secondPacket.getPts_time(), is(0.0));
    assertThat(secondPacket.getDts(), is(0L));
    assertThat(secondPacket.getDts_time(), is(0.0));
    assertThat(secondPacket.getDuration(), is(512L));
    assertThat(secondPacket.getDuration_time(), is(0.04F));
    assertThat(secondPacket.getSize(), is("105222"));
    assertThat(secondPacket.getPos(), is("5228"));
    assertThat(secondPacket.getFlags(), is("K_"));

    FFmpegPacket lastPacket = info.getPackets().get(info.getPackets().size() - 1);
    assertThat(lastPacket.getCodec_type(), is(CodecType.AUDIO));
    assertThat(lastPacket.getStream_index(), is(1));
    assertThat(lastPacket.getPts(), is(253952L));
    assertThat(lastPacket.getPts_time(), is(5.290667));
    assertThat(lastPacket.getDts(), is(253952L));
    assertThat(lastPacket.getDts_time(), is(5.290667));
    assertThat(lastPacket.getDuration(), is(1024L));
    assertThat(lastPacket.getDuration_time(), is(0.021333F));
    assertThat(lastPacket.getSize(), is("1111"));
    assertThat(lastPacket.getPos(), is("1054609"));
    assertThat(lastPacket.getFlags(), is("K_"));

    FFmpegFrame firstFrame = info.getFrames().get(0);
    assertThat(firstFrame.getStream_index(), is(1));
    assertThat(firstFrame.getKey_frame(), is(1));
    assertThat(firstFrame.getPkt_pts(), is(0L));
    assertThat(firstFrame.getPkt_pts_time(), is(0.0));
    assertThat(firstFrame.getPkt_dts(), is(0L));
    assertThat(firstFrame.getPkt_dts_time(), is(0.0));
    assertThat(firstFrame.getBest_effort_timestamp(), is(0L));
    assertThat(firstFrame.getBest_effort_timestamp_time(), is(0.0F));
    assertThat(firstFrame.getPkt_duration(), is(1024L));
    assertThat(firstFrame.getPkt_duration_time(), is(0.021333F));
    assertThat(firstFrame.getPkt_pos(), is(4261L));
    assertThat(firstFrame.getPkt_size(), is(967L));
    assertThat(firstFrame.getSample_fmt(), is("fltp"));
    assertThat(firstFrame.getNb_samples(), is(1024));
    assertThat(firstFrame.getChannels(), is(6));
    assertThat(firstFrame.getChannel_layout(), is("5.1"));

    FFmpegFrame secondFrame = info.getFrames().get(1);
    assertThat(secondFrame.getMedia_type(), is(CodecType.VIDEO));
    assertThat(secondFrame.getStream_index(), is(0));
    assertThat(secondFrame.getKey_frame(), is(1));
    assertThat(secondFrame.getPkt_pts(), is(0L));
    assertThat(secondFrame.getPkt_pts_time(), is(0.0));
    assertThat(secondFrame.getPkt_dts(), is(0L));
    assertThat(secondFrame.getPkt_dts_time(), is(0.0));
    assertThat(secondFrame.getBest_effort_timestamp(), is(0L));
    assertThat(secondFrame.getBest_effort_timestamp_time(), is(0.0F));
    assertThat(secondFrame.getPkt_duration(), is(512L));
    assertThat(secondFrame.getPkt_duration_time(), is(0.04F));
    assertThat(secondFrame.getPkt_pos(), is(5228L));
    assertThat(secondFrame.getPkt_size(), is(105222L));
    assertThat(secondFrame.getSample_fmt(), new IsNull<>());
    assertThat(secondFrame.getNb_samples(), is(0));
    assertThat(secondFrame.getChannels(), is(0));
    assertThat(secondFrame.getChannel_layout(), new IsNull<>());

    FFmpegFrame lastFrame = info.getFrames().get(info.getFrames().size() - 1);
    assertLastFrame(lastFrame);
  }

  private void assertLastFrame(FFmpegFrame actual) {
    assertThat(actual.getMedia_type(), is(CodecType.AUDIO));
    assertThat(actual.getStream_index(), is(1));
    assertThat(actual.getKey_frame(), is(1));
    assertThat(actual.getPkt_pts(), is(253952L));
    assertThat(actual.getPkt_pts_time(), is(5.290667));
    assertThat(actual.getPkt_dts(), is(253952L));
    assertThat(actual.getPkt_dts_time(), is(5.290667));
    assertThat(actual.getBest_effort_timestamp(), is(253952L));
    assertThat(actual.getBest_effort_timestamp_time(), is(5.290667F));
    assertThat(actual.getPkt_duration(), is(1024L));
    assertThat(actual.getPkt_duration_time(), is(0.021333F));
    assertThat(actual.getPkt_pos(), is(1054609L));
    assertThat(actual.getPkt_size(), is(1111L));
    assertThat(actual.getSample_fmt(), is("fltp"));
    assertThat(actual.getNb_samples(), is(1024));
    assertThat(actual.getChannels(), is(6));
    assertThat(actual.getChannel_layout(), is("5.1"));
  }

  @Test
  public void testProbeVideo2() throws IOException {
    FFmpegProbeResult info = ffprobe.probe(Samples.always_on_my_mind);
    assertFalse(info.hasError());

    // Only a quick sanity check until we do something better
    assertThat(info.getStreams(), hasSize(2));
    assertThat(info.getStreams().get(0).getCodec_type(), is(CodecType.VIDEO));
    assertThat(info.getStreams().get(1).getCodec_type(), is(CodecType.AUDIO));

    assertThat(info.getStreams().get(1).getChannels(), is(2));
    assertThat(info.getStreams().get(1).getSample_rate(), is(48_000));

    // Test a UTF-8 name
    assertThat(
        info.getFormat().getFilename(),
        is("c:\\Users\\Bob\\Always On My Mind [Program Only] - Adelén.mp4"));

    // System.out.println(FFmpegUtils.getGson().toJson(info));
  }

  @Test
  public void testProbeStartPts() throws IOException {
    FFmpegProbeResult info = ffprobe.probe(Samples.start_pts_test);
    assertFalse(info.hasError());

    // Check edge case with a time larger than an integer
    assertThat(info.getStreams().get(0).getStart_pts(), is(8570867078L));
  }

  @Test
  public void testProbeDivideByZero() throws IOException {
    // https://github.com/bramp/ffmpeg-cli-wrapper/issues/10
    FFmpegProbeResult info = ffprobe.probe(Samples.divide_by_zero);
    assertFalse(info.hasError());

    assertThat(info.getStreams().get(1).getCodec_time_base(), is(Fraction.ZERO));

    // System.out.println(FFmpegUtils.getGson().toJson(info));
  }

  @Test
  public void shouldThrowOnErrorWithFFmpegProbeResult() {
    Mockito.doReturn(1).when(mockProcess).exitValue();

    final FFmpegError error = new FFmpegError();
    final FFmpegProbeResult result = new FFmpegProbeResult();
    result.setError(error);
    FFmpegException e =
        assertThrows(FFmpegException.class, () -> ffprobe.throwOnError(mockProcess, result));
    assertEquals(error, e.getError());
  }

  @Test
  public void shouldThrowOnErrorEvenIfProbeResultHasNoError() {
    Mockito.doReturn(1).when(mockProcess).exitValue();

    final FFmpegProbeResult result = new FFmpegProbeResult();
    FFmpegException e =
        assertThrows(FFmpegException.class, () -> ffprobe.throwOnError(mockProcess, result));
    assertNull(e.getError());
  }

  @Test
  public void shouldThrowOnErrorEvenIfProbeResultIsNull() {
    Mockito.doReturn(1).when(mockProcess).exitValue();

    FFmpegException e =
        assertThrows(FFmpegException.class, () -> ffprobe.throwOnError(mockProcess, null));
    assertNull(e.getError());
  }

  @Test
  public void testShouldThrowErrorWithoutMock() throws IOException {
    FFprobe probe = new FFprobe();
    FFmpegException e = assertThrows(FFmpegException.class, () -> probe.probe("doesnotexist.mp4"));

    assertNotNull(e);
    assertNotNull(e.getError());

    // Intentionally not comparing the values, as those might change for different ffmpeg versions
    assertNotNull(e.getError().getString());
    assertNotEquals(0, e.getError().getCode());
  }

  @Test
  public void testProbeSideDataList() throws IOException {
    FFmpegProbeResult info = ffprobe.probe(Samples.side_data_list);

    // Check edge case with a time larger than an integer
    assertThat(info.getStreams().get(0).getSide_data_list().size(), is(1));
    assertThat(
        info.getStreams().get(0).getSide_data_list().get(0).getSide_data_type(), is("Display Matrix"));
    assertThat(
        info.getStreams().get(0).getSide_data_list().get(0).getDisplaymatrix(),
        is(
            "\n00000000:            0      -65536           0\n00000001:        65536           0           0\n00000002:            0           0  1073741824\n"));
    assertThat(info.getStreams().get(0).getSide_data_list().get(0).getRotation(), is(90));
  }

  @Test
  public void testChaptersWithLongIds() throws IOException {
    FFmpegProbeResult info = ffprobe.probe(Samples.chapters_with_long_id);

    assertThat(info.getChapters().get(0).getId(), is(6613449456311024506L));
    assertThat(info.getChapters().get(1).getId(), is(-4433436293284298339L));
  }

  @Test
  public void testProbeDefaultArguments() throws IOException {
    ffprobe.probe(Samples.always_on_my_mind);

    verify(runFunc, times(2)).run(argsCaptor.capture());

    List<String> value = Helper.subList(argsCaptor.getValue(), 1);

    assertThat(
        value,
        is(
            ImmutableList.of(
                "-v",
                "quiet",
                "-print_format",
                "json",
                "-show_error",
                "-show_format",
                "-show_streams",
                "-show_chapters",
                Samples.always_on_my_mind)));
  }

  @Test
  public void testProbeProbeBuilder() throws IOException {
    ffprobe.probe(new FFprobeBuilder().setInput(Samples.always_on_my_mind));

    verify(runFunc, times(2)).run(argsCaptor.capture());

    List<String> value = Helper.subList(argsCaptor.getValue(), 1);

    assertThat(
        value,
        is(
            ImmutableList.of(
                "-v",
                "quiet",
                "-print_format",
                "json",
                "-show_error",
                "-show_format",
                "-show_streams",
                "-show_chapters",
                Samples.always_on_my_mind)));
  }

  @Test
  public void testProbeProbeBuilderBuilt() throws IOException {
    ffprobe.probe(new FFprobeBuilder().setInput(Samples.always_on_my_mind).build());

    verify(runFunc, times(2)).run(argsCaptor.capture());

    List<String> value = Helper.subList(argsCaptor.getValue(), 1);

    assertThat(
        value,
        is(
            ImmutableList.of(
                "-v",
                "quiet",
                "-print_format",
                "json",
                "-show_error",
                "-show_format",
                "-show_streams",
                "-show_chapters",
                Samples.always_on_my_mind)));
  }

  @Test
  public void testProbeProbeExtraArgs() throws IOException {
    ffprobe.probe(Samples.always_on_my_mind, null, "-rw_timeout", "0");

    verify(runFunc, times(2)).run(argsCaptor.capture());

    List<String> value = Helper.subList(argsCaptor.getValue(), 1);

    assertThat(
        value,
        is(
            ImmutableList.of(
                "-v",
                "quiet",
                "-print_format",
                "json",
                "-show_error",
                "-rw_timeout",
                "0",
                "-show_format",
                "-show_streams",
                "-show_chapters",
                Samples.always_on_my_mind)));
  }

  @Test
  public void testProbeProbeUserAgent() throws IOException {
    ffprobe.probe(Samples.always_on_my_mind, "ffmpeg-cli-wrapper");

    verify(runFunc, times(2)).run(argsCaptor.capture());

    List<String> value = Helper.subList(argsCaptor.getValue(), 1);

    assertThat(
        value,
        is(
            ImmutableList.of(
                "-v",
                "quiet",
                "-print_format",
                "json",
                "-show_error",
                "-user_agent",
                "ffmpeg-cli-wrapper",
                "-show_format",
                "-show_streams",
                "-show_chapters",
                Samples.always_on_my_mind)));
  }

  @Test
  public void testFullFormatDeserialization() throws IOException {
    FFmpegFormat format = ffprobe.probe(Samples.always_on_my_mind).getFormat();

    assertThat(format.getFilename(), endsWith("Always On My Mind [Program Only] - Adelén.mp4"));
    assertEquals(2, format.getNb_streams());
    assertEquals(0, format.getNb_programs());
    assertEquals("mov,mp4,m4a,3gp,3g2,mj2", format.getFormat_name());
    assertEquals("QuickTime / MOV", format.getFormat_long_name());
    assertEquals(0d, format.getStart_time(), 0.01);
    assertEquals(181.632d, format.getDuration(), 0.01);
    assertEquals(417127573, format.getSize());
    assertEquals(18372426, format.getBit_rate());
    assertEquals(100, format.getProbe_score());
    assertEquals(4, format.getTags().size());

    assertEquals("mp42", format.getTags().get("major_brand"));
  }

  @Test
  public void testFullChaptersDeserialization() throws IOException {
    List<FFmpegChapter> chapters = ffprobe.probe(Samples.book_with_chapters).getChapters();
    FFmpegChapter chapter = chapters.get(chapters.size() - 1);

    assertEquals(24, chapters.size());

    assertEquals(23, chapter.getId());
    assertEquals("1/44100", chapter.getTime_base());
    assertEquals(237875790, chapter.getStart());
    assertEquals("5394.008844", chapter.getStart_time());
    assertEquals(248628224, chapter.getEnd());
    assertEquals("5637.828209", chapter.getEnd_time());
    assertEquals("24 - Chatterer Has His Turn to Laugh", chapter.getTags().getTitle());
  }

  @Test
  public void testFullVideoStreamDeserialization() throws IOException {
    List<FFmpegStream> streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).getStreams();
    FFmpegStream stream = streams.get(0);

    assertEquals(0, stream.getIndex());
    assertEquals("h264", stream.getCodec_name());
    assertEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", stream.getCodec_long_name());
    assertEquals("Main", stream.getProfile());
    assertEquals(CodecType.VIDEO, stream.getCodec_type());
    assertEquals(Fraction.getFraction(1, 50), stream.getCodec_time_base());
    assertEquals("avc1", stream.getCodec_tag_string());
    assertEquals("0x31637661", stream.getCodec_tag());
    assertEquals(1280, stream.getWidth());
    assertEquals(720, stream.getHeight());
    assertEquals(0, stream.getHas_b_frames());
    assertEquals("1:1", stream.getSample_aspect_ratio());
    assertEquals("16:9", stream.getDisplay_aspect_ratio());
    assertEquals("yuv420p", stream.getPix_fmt());
    assertEquals(31, stream.getLevel());
    assertEquals("left", stream.getChroma_location());
    assertEquals(1, stream.getRefs());
    assertEquals("true", stream.is_avc());
    assertEquals("4", stream.getNal_length_size());
    assertEquals("0x1", stream.getId());
    assertEquals(Fraction.getFraction(25, 1), stream.getR_frame_rate());
    assertEquals(Fraction.getFraction(25, 1), stream.getAvg_frame_rate());
    assertEquals(Fraction.getFraction(1, 12800), stream.getTime_base());
    assertEquals(0, stream.getStart_pts());
    assertEquals(0.0d, stream.getStart_time(), 0.01);
    assertEquals(67584, stream.getDuration_ts());
    assertEquals(5.28d, stream.getDuration(), 0.01);
    assertEquals(1205959, stream.getBit_rate());
    assertEquals(0, stream.getMax_bit_rate());
    assertEquals(8, stream.getBits_per_raw_sample());
    assertEquals(0, stream.getBits_per_sample());
    assertEquals(132, stream.getNb_frames());
    assertNull(stream.getSample_fmt());
    assertEquals(0, stream.getSample_rate());
    assertEquals(0, stream.getChannels());
    assertNull(stream.getChannel_layout());
    assertEquals(4, stream.getTags().size());
    assertEquals("und", stream.getTags().get("language"));
    assertEquals(0, stream.getSide_data_list().size());
  }

  @Test
  public void testFullAudioStreamDeserialization() throws IOException {
    List<FFmpegStream> streams = ffprobe.probe(Samples.big_buck_bunny_720p_1mb).getStreams();
    FFmpegStream stream = streams.get(1);

    assertEquals(1, stream.getIndex());
    assertEquals("aac", stream.getCodec_name());
    assertEquals("AAC (Advanced Audio Coding)", stream.getCodec_long_name());
    assertEquals("LC", stream.getProfile());
    assertEquals(CodecType.AUDIO, stream.getCodec_type());
    assertEquals(Fraction.getFraction(1, 48_000), stream.getCodec_time_base());
    assertEquals("mp4a", stream.getCodec_tag_string());
    assertEquals("0x6134706d", stream.getCodec_tag());
    assertEquals(0, stream.getWidth());
    assertEquals(0, stream.getHeight());
    assertEquals(0, stream.getHas_b_frames());
    assertNull(stream.getSample_aspect_ratio());
    assertNull(stream.getDisplay_aspect_ratio());
    assertNull(stream.getPix_fmt());
    assertEquals(0, stream.getLevel());
    assertNull(stream.getChroma_location());
    assertEquals(0, stream.getRefs());
    assertNull(stream.is_avc());
    assertNull(stream.getNal_length_size());
    assertEquals("0x2", stream.getId());
    assertEquals(Fraction.getFraction(0, 1), stream.getR_frame_rate());
    assertEquals(Fraction.getFraction(0, 1), stream.getAvg_frame_rate());
    assertEquals(Fraction.getFraction(1, 48_000), stream.getTime_base());
    assertEquals(0, stream.getStart_pts());
    assertEquals(0.0d, stream.getStart_time(), 0.01);
    assertEquals(254976, stream.getDuration_ts());
    assertEquals(5.312d, stream.getDuration(), 0.01);
    assertEquals(384828, stream.getBit_rate());
    assertEquals(400392, stream.getMax_bit_rate());
    assertEquals(0, stream.getBits_per_raw_sample());
    assertEquals(0, stream.getBits_per_sample());
    assertEquals(249, stream.getNb_frames());
    assertEquals("fltp", stream.getSample_fmt());
    assertEquals(48000, stream.getSample_rate());
    assertEquals(6, stream.getChannels());
    assertEquals("5.1", stream.getChannel_layout());
    assertEquals(4, stream.getTags().size());
    assertEquals("und", stream.getTags().get("language"));
    assertEquals(0, stream.getSide_data_list().size());
  }

  @Test
  public void testSideDataListDeserialization() throws IOException {
    List<FFmpegStream> streams = ffprobe.probe(Samples.side_data_list).getStreams();
    List<FFmpegStream.SideData> sideDataList = streams.get(0).getSide_data_list();

    assertEquals(1, sideDataList.size());
    assertEquals("Display Matrix", sideDataList.get(0).getSide_data_type());
    assertEquals(90, sideDataList.get(0).getRotation());
    assertThat(sideDataList.get(0).getDisplaymatrix(), startsWith("\n00000000:"));
  }

  @Test
  public void testDispositionDeserialization() throws IOException {
    List<FFmpegStream> streams = ffprobe.probe(Samples.side_data_list).getStreams();
    FFmpegDisposition disposition = streams.get(0).getDisposition();

    assertTrue(disposition.isDefault());
    assertFalse(disposition.isDub());
    assertFalse(disposition.isOriginal());
    assertFalse(disposition.isComment());
    assertFalse(disposition.isLyrics());
    assertFalse(disposition.isKaraoke());
    assertFalse(disposition.isForced());
    assertFalse(disposition.isHearingImpaired());
    assertFalse(disposition.isVisualImpaired());
    assertFalse(disposition.isCleanEffects());
    assertFalse(disposition.isAttachedPic());
    assertFalse(disposition.isCaptions());
    assertFalse(disposition.isDescriptions());
    assertFalse(disposition.isMetadata());
  }

  @Ignore("Broken until we fix mocking in Kotlin")
  @Test
  public void testDispositionWithAllFieldsTrueDeserialization() throws IOException {
    List<FFmpegStream> streams = ffprobe.probe(Samples.disposition_all_true).getStreams();
    FFmpegDisposition disposition = streams.get(0).getDisposition();

    assertTrue(disposition.isDefault());
    assertTrue(disposition.isDub());
    assertTrue(disposition.isOriginal());
    assertTrue(disposition.isComment());
    assertTrue(disposition.isLyrics());
    assertTrue(disposition.isKaraoke());
    assertTrue(disposition.isForced());
    assertTrue(disposition.isHearingImpaired());
    assertTrue(disposition.isVisualImpaired());
    assertTrue(disposition.isCleanEffects());
    assertTrue(disposition.isAttachedPic());
    assertTrue(disposition.isTimedThumbnails());
    assertTrue(disposition.isNonDiegetic());
    assertTrue(disposition.isCaptions());
    assertTrue(disposition.isDescriptions());
    assertTrue(disposition.isMetadata());
    assertTrue(disposition.isDependent());
    assertTrue(disposition.isStillImage());
  }

  @Test
  public void testFullPacketDeserialization() throws IOException {
    FFprobeBuilder probeBuilder =
        ffprobe
            .builder()
            .setShowPackets(true)
            .setInput(Samples.big_buck_bunny_720p_1mb_with_packets);
    List<FFmpegPacket> packets = ffprobe.probe(probeBuilder).getPackets();

    FFmpegPacket packet = packets.get(packets.size() - 1);

    assertEquals(CodecType.AUDIO, packet.getCodec_type());
    assertEquals(1, packet.getStream_index());
    assertEquals(253952, packet.getPts());
    assertEquals(5.290667, packet.getPts_time(), 0.0001);
    assertEquals(253952, packet.getDts());
    assertEquals(5.290667, packet.getDts_time(), 0.0001);
    assertEquals(1024, packet.getDuration());
    assertEquals(0.021333, packet.getDuration_time(), 0.0001);
    assertEquals("1111", packet.getSize());
    assertEquals("1054609", packet.getPos());
    assertEquals("K_", packet.getFlags());
  }

  @Test
  public void testFullFrameDeserialization() throws IOException {
    FFprobeBuilder probeBuilder =
        ffprobe.builder().setShowFrames(true).setInput(Samples.big_buck_bunny_720p_1mb_with_frames);
    List<FFmpegFrame> frames = ffprobe.probe(probeBuilder).getFrames();

    FFmpegFrame frame = frames.get(frames.size() - 1);

    assertEquals(CodecType.AUDIO, frame.getMedia_type());
    assertEquals(1, frame.getStream_index());
    assertEquals(1, frame.getKey_frame());
    assertEquals(253952, frame.getPkt_pts());
    assertEquals(5.290667, frame.getPkt_pts_time(), 0.0001);
    assertEquals(253952, frame.getPkt_dts());
    assertEquals(5.290667, frame.getPkt_dts_time(), 0.0001);
    assertEquals(253952, frame.getBest_effort_timestamp());
    assertEquals(5.290667, frame.getBest_effort_timestamp_time(), 0.0001);
    assertEquals(1024, frame.getPkt_duration());
    assertEquals(0.021333, frame.getPkt_duration_time(), 0.0001);
    assertEquals(1054609, frame.getPkt_pos());
    assertEquals(1111, frame.getPkt_size());
    assertEquals("fltp", frame.getSample_fmt());
    assertEquals(1024, frame.getNb_samples());
    assertEquals(6, frame.getChannels());
    assertEquals("5.1", frame.getChannel_layout());
  }
}
