package net.bramp.ffmpeg.progress;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public abstract class AbstractProgressParserTest {
  @Rule public Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

  final List<Progress> progresses = Collections.synchronizedList(new ArrayList<>());

  ProgressParser parser;
  URI uri;

  final ProgressListener listener = progresses::add;

  @Before
  public void setupParser() throws IOException, URISyntaxException {
    synchronized (progresses) {
      progresses.clear();
    }

    parser = newParser(listener);
    uri = parser.getUri();
  }

  public abstract ProgressParser newParser(ProgressListener listener)
      throws IOException, URISyntaxException;

  @Test
  public void testNoConnection() throws IOException {
    parser.start();
    parser.stop();
    assertTrue(progresses.isEmpty());
  }

  @Test
  public void testDoubleStop() throws IOException {
    parser.start();
    parser.stop();
    parser.stop();
    assertTrue(progresses.isEmpty());
  }

  @Test(expected = IllegalThreadStateException.class)
  public void testDoubleStart() throws IOException {
    parser.start();
    parser.start();
    assertTrue(progresses.isEmpty());
  }

  @Test()
  public void testStopNoStart() throws IOException {
    parser.stop();
    assertTrue(progresses.isEmpty());
  }
}
