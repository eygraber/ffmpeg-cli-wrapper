package net.bramp.ffmpeg.nut

import com.google.common.base.Charsets
import net.bramp.ffmpeg.nut.Packet.StartCode
import java.io.EOFException
import java.io.IOException
import java.io.InputStream

/**
 * Demuxer for the FFmpeg Nut file format.
 *
 *
 * Lots of things not implemented, startcode searching, crc checks, etc
 *
 * @see [https://www.ffmpeg.org/~michael/nut.txt](https://www.ffmpeg.org/~michael/nut.txt)
 *
 * @see [https://github.com/FFmpeg/FFmpeg/blob/master/libavformat/nutdec.c](https://github.com/FFmpeg/FFmpeg/blob/master/libavformat/nutdec.c)
 */
class NutReader(inputStream: InputStream, private val listener: NutReaderListener) {
  lateinit var header: MainHeaderPacket
  val streams: MutableList<Stream> = ArrayList()
  private val `in`: NutDataInputStream = NutDataInputStream(inputStream)

  /**
   * Read the magic at the beginning of the file.
   *
   * @throws IOException If a I/O error occurs
   */
  @Throws(IOException::class)
  private fun readFileId() {
    val b = ByteArray(HEADER.size)
    `in`.readFully(b)
    if(!b.contentEquals(HEADER)) {
      throw IOException(
        "file_id_string does not match. got: " + String(b, Charsets.ISO_8859_1),
      )
    }
  }

  /**
   * Read headers we don't know how to parse yet, returning the next startcode.
   *
   * @return The next startcode
   * @throws IOException If a I/O error occurs
   */
  @Throws(IOException::class)
  private fun readReservedHeaders(): Long {
    var startcode = `in`.readStartCode()
    while(StartCode.isPossibleStartcode(startcode) && isKnownStartcode(startcode)) {
      Packet().read(`in`, startcode) // Discard unknown packet
      startcode = `in`.readStartCode()
    }
    return startcode
  }

  /**
   * Demux the inputstream
   *
   * @throws IOException If a I/O error occurs
   */
  @Throws(IOException::class)
  fun read() {
    readFileId()
    `in`.resetCRC()
    try {
      var startcode = `in`.readStartCode()
      while (true) {
        val packet = StartCode.of(startcode)
        if (packet == null) {
          if (StartCode.isPossibleStartcode(startcode)) {
            throw IOException("expected framecode, found " + StartCode.toString(startcode))
          }

          // This is a frame packet
          val f = Frame()
          f.read(this, `in`, startcode.toInt())
          listener.frame(f)
          startcode = `in`.readStartCode()
          continue
        }
        when (packet) {
          StartCode.MAIN -> {
            header = MainHeaderPacket()
            if (!StartCode.MAIN.equalsCode(startcode)) {
              throw IOException(String.format("expected main header found: 0x%X", startcode))
            }
            header.read(`in`, startcode)
            startcode = `in`.readStartCode()
          }
          StartCode.STREAM -> {
            if (!StartCode.STREAM.equalsCode(startcode)) {
              throw IOException(String.format("expected stream header found: 0x%X", startcode))
            }
            val streamHeader = StreamHeaderPacket()
            streamHeader.read(`in`, startcode)
            val stream = Stream(header, streamHeader)
            streams.add(stream)
            listener.stream(stream)
            startcode = `in`.readStartCode()
          }
          StartCode.SYNCPOINT -> {
            Packet().read(`in`, startcode) // Discard for the moment
            startcode = `in`.readStartCode()
          }
          StartCode.INDEX -> {
            Packet().read(`in`, startcode) // Discard for the moment
            startcode = `in`.readStartCode()
          }
          StartCode.INFO -> {
            Packet().read(`in`, startcode) // Discard for the moment
            startcode = `in`.readStartCode()
          }
        }
      }
    }
    catch(_: EOFException) {
      // We are done
    }
  }

  companion object {
    // HEADER is the string "nut/multimedia container\0"
    val HEADER = byteArrayOf(
      0x6e,
      0x75,
      0x74,
      0x2f,
      0x6d,
      0x75,
      0x6c,
      0x74,
      0x69,
      0x6d,
      0x65,
      0x64,
      0x69,
      0x61,
      0x20,
      0x63,
      0x6f,
      0x6e,
      0x74,
      0x61,
      0x69,
      0x6e,
      0x65,
      0x72,
      0x00,
    )

    fun isKnownStartcode(startcode: Long): Boolean = StartCode.of(startcode) != null
  }
}
