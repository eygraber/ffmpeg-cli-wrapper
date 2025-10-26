package net.bramp.ffmpeg.kotlin.nut

class FrameCode {
  var flags: Long = 0
  var streamId = 0
  var dataSizeMul = 0
  var dataSizeLsb = 0
  var ptsDelta: Long = 0
  var reservedCount = 0
  var matchTimeDelta: Long = 0
  var headerIdx = 0

  override fun toString(): String =
    "FrameCode(flags=$flags, id=$streamId, dataSizeMul=$dataSizeMul, dataSizeLsb=$dataSizeLsb, " +
      "ptsDelta=$ptsDelta, reservedCount=$reservedCount, matchTimeDelta=$matchTimeDelta, headerIdx=$headerIdx)"
}
