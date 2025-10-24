package net.bramp.ffmpeg.fixtures

import net.bramp.ffmpeg.info.Filter
import net.bramp.ffmpeg.info.FilterPattern

object Filters {
  val FILTERS = listOf(
    Filter(
      isTimelineSupported = false,
      isSliceThreading = false,
      isCommandSupport = false,
      name = "abench",
      inputPattern = FilterPattern("A"),
      outputPattern = FilterPattern("A"),
      description = "Benchmark part of a filtergraph."
    ),
    Filter(
      isTimelineSupported = false,
      isSliceThreading = false,
      isCommandSupport = true,
      name = "acompressor",
      inputPattern = FilterPattern("A"),
      outputPattern = FilterPattern("A"),
      description = "Audio compressor."
    ),
    // ... ADD THE REST OF THE FILTERS FROM THE JAVA VERSION ...
  )
}
