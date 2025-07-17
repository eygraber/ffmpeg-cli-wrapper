package net.bramp.ffmpeg.nut

import com.google.common.base.MoreObjects
import org.apache.commons.lang3.math.Fraction
import java.io.IOException
import java.nio.charset.StandardCharsets

class StreamHeaderPacket : Packet() {
  var id = 0
  var type: Long = 0 // One of VIDEO/AUDIO/SUBTITLE/USER_DATA // TODO Convert to enum.
  lateinit var fourcc: ByteArray
  var timeBaseId = 0
  var msbPtsShift = 0
  var maxPtsDistance = 0
  var decodeDelay: Long = 0
  var flags: Long = 0
  lateinit var codecSpecificData: ByteArray

  // If video
  var width = 0
  var height = 0
  var sampleWidth = 0
  var sampleHeight = 0
  var colorspaceType: Long = 0

  // If audio
  var sampleRate: Fraction = Fraction.ZERO
  var channels = 0

  @Throws(IOException::class)
  override fun readBody(input: NutDataInputStream) {
    id = input.readVarInt()
    type = input.readVarLong()
    fourcc = input.readVarArray()
    if(fourcc.size != 2 && fourcc.size != 4) {
      // TODO In future fourcc could be a different size, but for sanity checking lets leave this
      // check in.
      throw IOException("Unexpected fourcc length: " + fourcc.size)
    }
    timeBaseId = input.readVarInt()
    msbPtsShift = input.readVarInt()
    if(msbPtsShift >= 16) {
      throw IOException("invalid msbPtsShift $msbPtsShift want < 16")
    }
    maxPtsDistance = input.readVarInt()
    decodeDelay = input.readVarLong()
    flags = input.readVarLong()
    codecSpecificData = input.readVarArray()
    if(type == VIDEO.toLong()) {
      width = input.readVarInt()
      height = input.readVarInt()
      if(width == 0 || height == 0) {
        throw IOException("invalid video dimensions " + width + "x" + height)
      }
      sampleWidth = input.readVarInt()
      sampleHeight = input.readVarInt()

      // Both MUST be 0 if unknown otherwise both MUST be nonzero.
      if((sampleWidth == 0 || sampleHeight == 0) && sampleWidth != sampleHeight) {
        throw IOException(
          "invalid video sample dimensions " + sampleWidth + "x" + sampleHeight,
        )
      }
      colorspaceType = input.readVarLong()
    }
    else if(type == AUDIO.toLong()) {
      val samplerateNum = input.readVarInt()
      val samplerateDenom = input.readVarInt()
      sampleRate = Fraction.getFraction(samplerateNum, samplerateDenom)
      channels = input.readVarInt()
    }
  }

  override fun toString(): String = MoreObjects.toStringHelper(this)
    .add("header", header)
    .add("id", id)
    .add("type", type)
    .add("fourcc", fourccToString(fourcc))
    .add("timeBaseId", timeBaseId)
    .add("msbPtsShift", msbPtsShift)
    .add("maxPtsDistance", maxPtsDistance)
    .add("decodeDelay", decodeDelay)
    .add("flags", flags)
    .add("codecSpecificData", codecSpecificData)
    .add("width", width)
    .add("height", height)
    .add("sampleWidth", sampleWidth)
    .add("sampleHeight", sampleHeight)
    .add("colorspaceType", colorspaceType)
    .add("sampleRate", sampleRate)
    .add("channels", channels)
    .add("footer", footer)
    .toString()

  companion object {
    const val VIDEO = 0
    const val AUDIO = 1
    const val SUBTITLE = 2
    const val USER_DATA = 3
    fun fourccToString(fourcc: ByteArray): String = String(fourcc, StandardCharsets.ISO_8859_1)
  }
}
