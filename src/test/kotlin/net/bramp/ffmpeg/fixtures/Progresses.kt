package net.bramp.ffmpeg.fixtures

import net.bramp.ffmpeg.progress.Progress
import org.apache.commons.lang3.math.Fraction

val allFiles = listOf("ffmpeg-progress-0", "ffmpeg-progress-1", "ffmpeg-progress-2")

val naProgressFile = listOf("ffmpeg-progress-na")

val allProgresses = listOf(
  Progress(
    5,
    Fraction.getFraction(0.0),
    800,
    48,
    512_000_000,
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
    5_034_667_000L,
    0,
    0,
    -1f,
    Progress.Status.Continue,
  ),
  Progress(
    132,
    Fraction.getFraction(23.1),
    1_935_500,
    1_285_168,
    5_312_000_000L,
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
