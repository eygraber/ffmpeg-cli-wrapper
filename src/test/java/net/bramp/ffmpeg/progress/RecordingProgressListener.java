package net.bramp.ffmpeg.progress;

import com.google.common.collect.Lists;
import java.util.List;

/** Test class to keep a record of all progresses. */
public class RecordingProgressListener implements ProgressListener {
  public final List<Progress> progresses = Lists.newArrayList();

  @Override
  public void progress(Progress p) {
    progresses.add(p);
  }

  public void reset() {
    progresses.clear();
  }
}
