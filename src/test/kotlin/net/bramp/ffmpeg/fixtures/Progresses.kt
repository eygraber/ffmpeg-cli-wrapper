package net.bramp.ffmpeg.fixtures

import net.bramp.ffmpeg.progress.Progress
import org.apache.commons.lang3.math.Fraction

object Progresses {
  val allFiles = listOf("ffmpeg-progress-0", "ffmpeg-progress-1", "ffmpeg-progress-2")

  val naProgressFile = listOf("ffmpeg-progress-na")

  val allProgresses = listOf(
    Progress(
      5,
      Fraction.getFraction(0.0),
      800,
      48,
      512000000,
      0,
      0,
      1.01f,
      Progress.Status.Continue,
    ),
    Progress(
      118,
      Fraction.getFraction(23.4),
      -1,
      -1,
      5034667000L,
      0,
      0,
      -1f,
      Progress.Status.Continue,
    ),
    Progress(
      132,
      Fraction.getFraction(23.1),
      1935500,
      1285168,
      5312000000L,
      0,
      0,
      0.929f,
      Progress.Status.End,
    ),
  )

  val naProgresses = listOf(
    Progress(
      0,
      Fraction.getFraction(0.0),
      -1,
      -1,
      -1,
      0,
      0,
      -1f,
      Progress.Status.End,
    ),
  )
}
