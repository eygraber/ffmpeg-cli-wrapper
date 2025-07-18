package net.bramp.ffmpeg.nut

import com.google.common.base.Preconditions
import net.bramp.ffmpeg.nut.StreamHeaderPacket.Companion.fourccToString
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream

object RawHandler {
  private fun bytesToInts(bytes: ByteArray): IntArray {
    val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer()
    val data = IntArray(buf.capacity())
    buf[data]
    return data
  }

  fun toBufferedImage(frame: Frame): BufferedImage {
    val header = frame.stream.header
    Preconditions.checkArgument(header.type == StreamHeaderPacket.VIDEO.toLong())

    // DataBufferByte buffer = new DataBufferByte(frame.data, frame.data.length);
    // SampleModel sample = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
    // streamHeader.width, streamHeader.height, 1);
    // Raster raster = new Raster(sample, buffer, new Point(0,0));
    val type = BufferedImage.TYPE_INT_ARGB // TODO Use the type defined in the stream header.
    val img = BufferedImage(header.width, header.height, type)

    // TODO Avoid this conversion.
    val data = bytesToInts(frame.data)
    val stride = header.width // TODO Check this is true
    img.setRGB(0, 0, header.width, header.height, data, 0, stride)
    return img
  }

  /**
   * Parses a FourCC into a AudioEncoding based on the following rules:<br></br>
   * "ALAW" = A-LAW<br></br>
   * "ULAW" = MU-LAW<br></br>
   * P[type][interleaving][bits] = little-endian PCM<br></br>
   * [bits][interleaving][type]P = big-endian PCM<br></br>
   * Where:<br></br>
   * &nbsp;&nbsp;[type] is S for signed integer, U for unsigned integer, F for IEEE float<br></br>
   * &nbsp;&nbsp;[interleaving] is D for default, P is for planar.<br></br>
   * &nbsp;&nbsp;[bits] is 8/16/24/32<br></br>
   *
   * @param header The stream's header.
   * @return The AudioFormat matching this header.
   */
  @Suppress("UseRequire")
  fun streamToAudioFormat(header: StreamHeaderPacket): AudioFormat {
    require(header.type == StreamHeaderPacket.AUDIO.toLong())
    require(header.fourcc.size == 4) {
      "unknown fourcc value: '${fourccToString(header.fourcc)}'"
    }

    val alaw = byteArrayOf('A'.code.toByte(), 'L'.code.toByte(), 'A'.code.toByte(), 'W'.code.toByte())
    val ulaw = byteArrayOf('U'.code.toByte(), 'L'.code.toByte(), 'A'.code.toByte(), 'W'.code.toByte())

    // Vars that go into the AudioFormat
    val encoding: AudioFormat.Encoding
    val sampleRate = header.sampleRate.toFloat()
    var bits = 8
    var isBigEndian = false
    val fourcc = header.fourcc

    if(fourcc.contentEquals(alaw)) {
      encoding = AudioFormat.Encoding.ALAW
    }
    else if(fourcc.contentEquals(ulaw)) {
      encoding = AudioFormat.Encoding.ULAW
    }
    else {
      val type: Byte
      val interleaving: Byte

      val isFourCCLittleEndian = fourcc[0] == 'P'.code.toByte()
      val isFourCCBigEndian = fourcc[3] == 'P'.code.toByte()

      require(isFourCCLittleEndian || isFourCCBigEndian) {
        "unknown fourcc value: '${fourccToString(fourcc)}'"
      }

      if(isFourCCLittleEndian) {
        isBigEndian = false
        type = fourcc[1]
        interleaving = fourcc[2]
        bits = fourcc[3].toInt()
      }
      else {
        isBigEndian = true
        type = fourcc[2]
        interleaving = fourcc[1]
        bits = fourcc[0].toInt()
      }

      require(interleaving == 'D'.code.toByte()) {
        "unsupported interleaving '$interleaving' in fourcc value '${fourccToString(fourcc)}'"
      }

      when(type) {
        'S'.code.toByte() -> encoding = AudioFormat.Encoding.PCM_SIGNED
        'U'.code.toByte() -> encoding = AudioFormat.Encoding.PCM_UNSIGNED
        'F'.code.toByte() -> encoding = AudioFormat.Encoding.PCM_FLOAT
        else -> throw IllegalArgumentException(
          "unknown fourcc '${fourccToString(fourcc)}' type: $type",
        )
      }
    }

    val frameSize = bits * header.channels / 8
    val frameRate = sampleRate // This may not be true for the compressed formats
    return AudioFormat(
      encoding,
      sampleRate,
      bits,
      header.channels,
      frameSize,
      frameRate,
      isBigEndian,
    )
  }

  fun toAudioInputStream(frame: Frame): AudioInputStream {
    val header = frame.stream.header
    Preconditions.checkArgument(header.type == StreamHeaderPacket.AUDIO.toLong())
    val format = streamToAudioFormat(header)
    val stream = ByteArrayInputStream(frame.data)
    return AudioInputStream(stream, format, (frame.data.size / format.frameSize).toLong())
  }
}
