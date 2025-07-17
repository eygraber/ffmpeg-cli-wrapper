package net.bramp.ffmpeg.nut

import com.google.common.base.MoreObjects
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

open class Packet {
  enum class StartCode(private val startcode: Long) {
    MAIN(0x7A561F5F04ADL + ((('N'.code shl 8).toLong() + 'M'.code.toLong()) shl 48)),
    STREAM(0x11405BF2F9DBL + ((('N'.code shl 8).toLong() + 'S'.code.toLong()) shl 48)),
    SYNCPOINT(0xE4ADEECA4569L + ((('N'.code shl 8).toLong() + 'K'.code.toLong()) shl 48)),
    INDEX(0xDD672F23E64EL + ((('N'.code shl 8).toLong() + 'X'.code.toLong()) shl 48)),
    INFO(0xAB68B596BA78L + ((('N'.code shl 8).toLong() + 'I'.code.toLong()) shl 48)),
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

      fun isPossibleStartcode(startcode: Long): Boolean = (startcode and 0xFFL) == 'N'.code.toLong()

      fun toString(startcode: Long): String {
        val c = of(startcode)
        if(c != null) {
          return c.name
        }
        return String.format("%X", startcode)
      }
    }
  }

  val header: PacketHeader = PacketHeader()
  val footer: PacketFooter = PacketFooter()

  @Throws(IOException::class)
  protected open fun readBody(`in`: NutDataInputStream) {
    // Default implementation does nothing
  }

  @Throws(IOException::class)
  fun read(`in`: NutDataInputStream, startcode: Long) {
    header.read(`in`, startcode)
    readBody(`in`)
    seekToPacketFooter(`in`)
    footer.read(`in`)
  }

  @Throws(IOException::class)
  fun seekToPacketFooter(`in`: NutDataInputStream) {
    val current = `in`.offset()
    if(current > header.end) {
      throw IOException("Can not seek backwards at:" + current + " end:" + header.end)
    }
    // TODO Fix this to not cast longs to ints
    `in`.skipBytes((header.end - current).toInt())
  }

  override fun toString(): String = MoreObjects.toStringHelper(this).add("header", header).add("footer", footer)
    .toString()

  companion object {
    val LOG: Logger = LoggerFactory.getLogger(Packet::class.java)
  }
}
