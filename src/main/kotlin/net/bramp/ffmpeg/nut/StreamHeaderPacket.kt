package net.bramp.ffmpeg.nut

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
  override fun readBody(dataInputStream: NutDataInputStream) {
    id = dataInputStream.readVarInt()
    type = dataInputStream.readVarLong()
    fourcc = dataInputStream.readVarArray()
    if(fourcc.size != 2 && fourcc.size != 4) {
      // TODO In future fourcc could be a different size, but for sanity checking lets leave this
      // check in.
      throw IOException("Unexpected fourcc length: " + fourcc.size)
    }
    timeBaseId = dataInputStream.readVarInt()
    msbPtsShift = dataInputStream.readVarInt()
    if(msbPtsShift >= 16) {
      throw IOException("invalid msbPtsShift $msbPtsShift want < 16")
    }
    maxPtsDistance = dataInputStream.readVarInt()
    decodeDelay = dataInputStream.readVarLong()
    flags = dataInputStream.readVarLong()
    codecSpecificData = dataInputStream.readVarArray()
    if(type == VIDEO.toLong()) {
      width = dataInputStream.readVarInt()
      height = dataInputStream.readVarInt()
      if(width == 0 || height == 0) {
        throw IOException("invalid video dimensions " + width + "x" + height)
      }
      sampleWidth = dataInputStream.readVarInt()
      sampleHeight = dataInputStream.readVarInt()

      // Both MUST be 0 if unknown otherwise both MUST be nonzero.
      if((sampleWidth == 0 || sampleHeight == 0) && sampleWidth != sampleHeight) {
        throw IOException(
          "invalid video sample dimensions " + sampleWidth + "x" + sampleHeight,
        )
      }
      colorspaceType = dataInputStream.readVarLong()
    }
    else if(type == AUDIO.toLong()) {
      val samplerateNum = dataInputStream.readVarInt()
      val samplerateDenom = dataInputStream.readVarInt()
      sampleRate = Fraction.getFraction(samplerateNum, samplerateDenom)
      channels = dataInputStream.readVarInt()
    }
  }

  override fun toString(): String =
    "StreamHeaderPacket(id=$id, type=$type, fourcc=${fourccToString(fourcc)}, " +
    "timeBaseId=$timeBaseId, msbPtsShift=$msbPtsShift, maxPtsDistance=$maxPtsDistance, " +
    "decodeDelay=$decodeDelay, flags=$flags, codecSpecificData=${codecSpecificData.contentToString()}, " +
    "width=$width, height=$height, " +
    "sampleWidth=$sampleWidth, sampleHeight=$sampleHeight, colorspaceType=$colorspaceType, " +
    "sampleRate=$sampleRate, channels=$channels)"

  companion object {
    const val VIDEO = 0
    const val AUDIO = 1
    const val SUBTITLE = 2
    const val USER_DATA = 3
    fun fourccToString(fourcc: ByteArray): String = String(fourcc, StandardCharsets.ISO_8859_1)
  }
}
