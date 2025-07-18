package net.bramp.ffmpeg;

import static net.bramp.ffmpeg.FFmpegTest.argThatHasItem;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.bramp.ffmpeg.lang.MockProcess;
import net.bramp.ffmpeg.lang.NewProcessAnswer;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests what happens when using avconv */
@RunWith(MockitoJUnitRunner.class)
public class FFmpegAvTest {

  ProcessFunction runFunc = args -> new MockProcess(
          Helper.loadResource("avconv-version")
  );

  FFmpeg ffmpeg;

  @Before
  public void before() {
    ffmpeg = new FFmpeg(FFmpeg.DEFAULT_PATH, runFunc);
  }

  @Test
  public void testVersion() throws Exception {
    assertEquals(
        "avconv version 11.4, Copyright (c) 2000-2014 the Libav developers", ffmpeg.version());
    assertEquals(
        "avconv version 11.4, Copyright (c) 2000-2014 the Libav developers", ffmpeg.version());
  }

  /** We don't support avconv, so all methods should throw an exception. */
  @Test(expected = IllegalArgumentException.class)
  public void testProbeVideo() throws IOException {
    ffmpeg.run(Collections.<String>emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCodecs() throws IOException {
    ffmpeg.codecs();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormats() throws IOException {
    ffmpeg.formats();
  }
}
