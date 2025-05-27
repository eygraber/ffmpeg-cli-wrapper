package net.bramp.ffmpeg.nut

import com.google.common.base.MoreObjects
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.Locale

open class Packet {
  enum class StartCode(private val startcode: Long) {
    Main(0x7A561F5F04ADL + (('N'.code.toLong() shl 8) + 'M'.code.toLong() shl 48)),
    Stream(0x11405BF2F9DBL + (('N'.code.toLong() shl 8) + 'S'.code.toLong() shl 48)),
    SyncPoint(0xE4ADEECA4569L + (('N'.code.toLong() shl 8) + 'K'.code.toLong() shl 48)),
    Index(0xDD672F23E64EL + (('N'.code.toLong() shl 8) + 'X'.code.toLong() shl 48)),
    Info(0xAB68B596BA78L + (('N'.code.toLong() shl 8) + 'I'.code.toLong() shl 48)),
    ;

    fun value(): Long = startcode

    fun equalsCode(startcode: Long): Boolean = this.startcode == startcode

    companion object {
      /**
       * Returns the Startcode enum for this code.
       *
       * @param startcode The numeric code for this Startcode.
       * @return The Startcode
       */
      fun of(startcode: Long): StartCode? {
        for(c in entries) {
          if (c.equalsCode(startcode)) {
            return c
          }
        }
        return null
      }

      fun isPossibleStartcode(startcode: Long): Boolean = startcode and 0xFFL == 'N'.code.toLong()

      fun toString(startcode: Long): String {
        val c = of(startcode)
        if(c != null) {
          return c.name
        }
        return String.format(Locale.ROOT, "%X", startcode)
      }
    }
  }

  val header: PacketHeader = PacketHeader()
  val footer: PacketFooter = PacketFooter()

  @Throws(IOException::class)
  protected open fun readBody(dataInputStream: NutDataInputStream) {
    // Default implementation does nothing
  }

  @Throws(IOException::class)
  fun read(dataInputStream: NutDataInputStream, startcode: Long) {
    header.read(dataInputStream, startcode)
    readBody(dataInputStream)
    seekToPacketFooter(dataInputStream)
    footer.read(dataInputStream)
  }

  @Throws(IOException::class)
  fun seekToPacketFooter(dataInputStream: NutDataInputStream) {
    val current = dataInputStream.offset()
    if(current > header.end) {
      throw IOException("Can not seek backwards at:$current end:${header.end}")
    }
    // TODO Fix this to not cast longs to ints
    dataInputStream.skipBytes((header.end - current).toInt())
  }

  override fun toString(): String = MoreObjects.toStringHelper(this).add("header", header).add("footer", footer)
    .toString()

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(Packet::class.java)
  }
}
