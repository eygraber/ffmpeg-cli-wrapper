package net.bramp.ffmpeg.nut

import com.google.common.base.MoreObjects

class FrameCode {
  var flags: Long = 0
  var streamId = 0
  var dataSizeMul = 0
  var dataSizeLsb = 0
  var ptsDelta: Long = 0
  var reservedCount = 0
  var matchTimeDelta: Long = 0
  var headerIdx = 0
  override fun toString(): String = MoreObjects.toStringHelper(this)
    .add("flags", flags)
    .add("id", streamId)
    .add("dataSizeMul", dataSizeMul)
    .add("dataSizeLsb", dataSizeLsb)
    .add("ptsDelta", ptsDelta)
    .add("reservedCount", reservedCount)
    .add("matchTimeDelta", matchTimeDelta)
    .add("headerIdx", headerIdx)
    .toString()
}
