package net.bramp.ffmpeg.nut

import com.google.common.io.CountingInputStream
import net.bramp.ffmpeg.io.CRC32InputStream
import java.io.DataInput
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream

/** A DataInputStream that implements a couple of custom FFmpeg Nut datatypes.  */
class NutDataInputStream(inputStream: InputStream) : DataInput {
  val dataInputStream: DataInputStream
  val crc: CRC32InputStream
  val count: CountingInputStream = CountingInputStream(inputStream)

  // These are for debugging, remove later
  var startCrcRange: Long = 0
  var endCrcRange: Long = 0

  init {
    this.crc = CRC32InputStream(count)
    this.dataInputStream = DataInputStream(crc)
  }

  fun resetCRC() {
    startCrcRange = count.count
    crc.resetCrc()
  }

  fun getCRC(): Long {
    endCrcRange = count.count
    return crc.value
  }

  // Read a simple var int up to 32 bits
  @Throws(IOException::class)
  fun readVarInt(): Int {
    var hasMore: Boolean
    var result = 0
    do {
      val b = dataInputStream.readUnsignedByte()
      hasMore = b and 0x80 == 0x80
      result = 128 * result + (b and 0x7F)

      // TODO Check for int overflow
    } while(hasMore)

    return result
  }

  // Read a simple var int up to 64 bits
  @Throws(IOException::class)
  fun readVarLong(): Long {
    var hasMore: Boolean
    var result: Long = 0
    do {
      val b = dataInputStream.readUnsignedByte()
      hasMore = b and 0x80 == 0x80
      result = 128 * result + (b and 0x7F)

      // TODO Check for long overflow
    } while(hasMore)

    return result
  }

  // Read a signed var int
  @Throws(IOException::class)
  fun readSignedVarInt(): Long {
    val temp = readVarLong() + 1
    if(temp and 1L == 1L) {
      return -(temp shr 1)
    }
    return temp shr 1
  }

  // Read a array with a varint prefixed length
  @Throws(IOException::class)
  fun readVarArray(): ByteArray {
    val len = readVarLong().toInt()
    val result = ByteArray(len)
    dataInputStream.read(result)
    return result
  }

  // Returns the start code, OR frame_code if the code doesn't start with 'N'
  @Throws(IOException::class)
  fun readStartCode(): Long {
    val frameCode = dataInputStream.readByte()
    if(frameCode != 'N'.code.toByte()) {
      return (frameCode.toInt() and 0xff).toLong()
    }

    // Otherwise read the remaining 64bit startCode
    val buffer = ByteArray(8)
    buffer[0] = frameCode
    readFully(buffer, 1, 7)
    return (buffer[0].toLong() shl 56) +
      (buffer[1].toLong() and 0xff shl 48) +
      (buffer[2].toLong() and 0xff shl 40) +
      (buffer[3].toLong() and 0xff shl 32) +
      (buffer[4].toLong() and 0xff shl 24) +
      (buffer[5].toInt() and 0xff shl 16) +
      (buffer[6].toInt() and 0xff shl 8) +
      (buffer[7].toInt() and 0xff)
  }

  fun offset(): Long = count.getCount()

  @Throws(IOException::class)
  override fun readFully(b: ByteArray) {
    dataInputStream.readFully(b)
  }

  @Throws(IOException::class)
  override fun readFully(b: ByteArray, off: Int, len: Int) {
    dataInputStream.readFully(b, off, len)
  }

  @Throws(IOException::class)
  override fun skipBytes(n: Int): Int = dataInputStream.skipBytes(n)

  @Throws(IOException::class)
  override fun readBoolean(): Boolean = dataInputStream.readBoolean()

  @Throws(IOException::class)
  override fun readByte(): Byte = dataInputStream.readByte()

  @Throws(IOException::class)
  override fun readUnsignedByte(): Int = dataInputStream.readUnsignedByte()

  @Throws(IOException::class)
  override fun readShort(): Short = dataInputStream.readShort()

  @Throws(IOException::class)
  override fun readUnsignedShort(): Int = dataInputStream.readUnsignedShort()

  @Throws(IOException::class)
  override fun readChar(): Char = dataInputStream.readChar()

  @Throws(IOException::class)
  override fun readInt(): Int = dataInputStream.readInt()

  @Throws(IOException::class)
  override fun readLong(): Long = dataInputStream.readLong()

  @Throws(IOException::class)
  override fun readFloat(): Float = dataInputStream.readFloat()

  @Throws(IOException::class)
  override fun readDouble(): Double = dataInputStream.readDouble()

  @Suppress("Deprecation")
  @Deprecated("")
  @Throws(IOException::class)
  override fun readLine(): String? = dataInputStream.readLine()

  @Throws(IOException::class)
  override fun readUTF(): String = dataInputStream.readUTF()
}
