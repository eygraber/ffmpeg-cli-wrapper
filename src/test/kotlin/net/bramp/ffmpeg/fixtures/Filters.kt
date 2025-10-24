package net.bramp.ffmpeg.fixtures

import net.bramp.ffmpeg.info.Filter
import net.bramp.ffmpeg.info.FilterPattern

object Filters {
  val FILTERS = listOf(
    Filter(false, false, false, "abench", FilterPattern("A"), FilterPattern("A"), "Benchmark part of a filtergraph."),
    Filter(false, false, true, "acompressor", FilterPattern("A"), FilterPattern("A"), "Audio compressor."),
    // ... ADD THE REST OF THE FILTERS FROM THE JAVA VERSION ...
  )
}
