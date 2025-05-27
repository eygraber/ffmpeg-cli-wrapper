package net.bramp.ffmpeg.nut

import org.apache.commons.lang3.math.Fraction
import java.io.IOException

class Stream(mainHeader: MainHeaderPacket, val header: StreamHeaderPacket) {
  val timeBase: Fraction
  var lastPts: Long = 0

  init {
    if(header.timeBaseId >= mainHeader.timeBase.size) {
      throw IOException(
        "Invalid timeBaseId " + header.timeBaseId + " must be < " + mainHeader.timeBase.size,
      )
    }
    timeBase = mainHeader.timeBase[header.timeBaseId]
  }
}
