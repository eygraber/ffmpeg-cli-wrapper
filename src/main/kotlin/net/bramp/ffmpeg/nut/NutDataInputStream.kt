package net.bramp.ffmpeg.nut

import com.google.common.base.Preconditions
import com.google.common.io.CountingInputStream
import net.bramp.ffmpeg.io.CRC32InputStream
import java.io.DataInput
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream

/** A DataInputStream that implements a couple of custom FFmpeg Nut datatypes.  */
class NutDataInputStream(`in`: InputStream?) : DataInput {
  val `in`: DataInputStream
  val crc: CRC32InputStream
  val count: CountingInputStream

  // These are for debugging, remove later
  var startCrcRange: Long = 0
  var endCrcRange: Long = 0

  init {
    Preconditions.checkNotNull<InputStream?>(`in`)
    this.count = CountingInputStream(`in`)
    this.crc = CRC32InputStream(count)
    this.`in` = DataInputStream(crc)
  }

  fun resetCRC() {
    startCrcRange = count.getCount()
    crc.resetCrc()
  }

  fun getCRC(): Long {
    endCrcRange = count.count
    return crc.value
  }

  // Read a simple var int up to 32 bits
  @Throws(IOException::class)
  fun readVarInt(): Int {
    var more: Boolean
    var result = 0
    do {
      val b = `in`.readUnsignedByte()
      more = (b and 0x80) == 0x80
      result = 128 * result + (b and 0x7F)

      // TODO Check for int overflow
    } while(more)

    return result
  }

  // Read a simple var int up to 64 bits
  @Throws(IOException::class)
  fun readVarLong(): Long {
    var more: Boolean
    var result: Long = 0
    do {
      val b = `in`.readUnsignedByte()
      more = (b and 0x80) == 0x80
      result = 128 * result + (b and 0x7F)

      // TODO Check for long overflow
    } while(more)

    return result
  }

  // Read a signed var int
  @Throws(IOException::class)
  fun readSignedVarInt(): Long {
    val temp = readVarLong() + 1
    if((temp and 1L) == 1L) {
      return -(temp shr 1)
    }
    return temp shr 1
  }

  // Read a array with a varint prefixed length
  @Throws(IOException::class)
  fun readVarArray(): ByteArray {
    val len = readVarLong().toInt()
    val result = ByteArray(len)
    `in`.read(result)
    return result
  }

  // Returns the start code, OR frame_code if the code doesn't start with 'N'
  @Throws(IOException::class)
  fun readStartCode(): Long {
    val frameCode = `in`.readByte()
    if(frameCode != 'N'.code.toByte()) {
      return (frameCode.toInt() and 0xff).toLong()
    }

    // Otherwise read the remaining 64bit startCode
    val buffer = ByteArray(8)
    buffer[0] = frameCode
    readFully(buffer, 1, 7)
    return (
      (
        (buffer[0].toLong() shl 56) +
          ((buffer[1].toInt() and 255).toLong() shl 48) +
          ((buffer[2].toInt() and 255).toLong() shl 40) +
          ((buffer[3].toInt() and 255).toLong() shl 32) +
          ((buffer[4].toInt() and 255).toLong() shl 24) +
          ((buffer[5].toInt() and 255) shl 16) +
          ((buffer[6].toInt() and 255) shl 8) +
          ((buffer[7].toInt() and 255) shl 0)
        )
      )
  }

  fun offset(): Long = count.getCount()

  @Throws(IOException::class)
  override fun readFully(b: ByteArray) {
    `in`.readFully(b)
  }

  @Throws(IOException::class)
  override fun readFully(b: ByteArray, off: Int, len: Int) {
    `in`.readFully(b, off, len)
  }

  @Throws(IOException::class)
  override fun skipBytes(n: Int): Int = `in`.skipBytes(n)

  @Throws(IOException::class)
  override fun readBoolean(): Boolean = `in`.readBoolean()

  @Throws(IOException::class)
  override fun readByte(): Byte = `in`.readByte()

  @Throws(IOException::class)
  override fun readUnsignedByte(): Int = `in`.readUnsignedByte()

  @Throws(IOException::class)
  override fun readShort(): Short = `in`.readShort()

  @Throws(IOException::class)
  override fun readUnsignedShort(): Int = `in`.readUnsignedShort()

  @Throws(IOException::class)
  override fun readChar(): Char = `in`.readChar()

  @Throws(IOException::class)
  override fun readInt(): Int = `in`.readInt()

  @Throws(IOException::class)
  override fun readLong(): Long = `in`.readLong()

  @Throws(IOException::class)
  override fun readFloat(): Float = `in`.readFloat()

  @Throws(IOException::class)
  override fun readDouble(): Double = `in`.readDouble()

  @Deprecated("")
  @Throws(IOException::class)
  override fun readLine(): String? = `in`.readLine()

  @Throws(IOException::class)
  override fun readUTF(): String = `in`.readUTF()
}
