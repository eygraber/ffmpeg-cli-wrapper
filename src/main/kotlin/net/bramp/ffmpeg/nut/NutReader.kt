package net.bramp.ffmpeg.nut

import net.bramp.ffmpeg.nut.Packet.StartCode
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

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
  val streams: MutableList<Stream> = mutableListOf()
  private val dataInputStream: NutDataInputStream = NutDataInputStream(inputStream)

  /**
   * Read the magic at the beginning of the file.
   *
   * @throws IOException If a I/O error occurs
   */
  @Throws(IOException::class)
  private fun readFileId() {
    val b = ByteArray(HEADER.size)
    dataInputStream.readFully(b)
    if(!b.contentEquals(HEADER)) {
      throw IOException(
        "file_id_string does not match. got: " + String(b, StandardCharsets.ISO_8859_1),
      )
    }
  }

  /**
   * Demux the inputstream
   *
   * @throws IOException If a I/O error occurs
   */
  @Throws(IOException::class)
  fun read() {
    readFileId()
    dataInputStream.resetCRC()
    try {
      var startcode = dataInputStream.readStartCode()
      while (true) {
        val packet = StartCode.of(startcode)
        if (packet == null) {
          if (StartCode.isPossibleStartcode(startcode)) {
            throw IOException("expected framecode, found " + StartCode.toString(startcode))
          }

          // This is a frame packet
          val f = Frame()
          f.read(this, dataInputStream, startcode.toInt())
          listener.frame(f)
          startcode = dataInputStream.readStartCode()
          continue
        }
        when (packet) {
          StartCode.Main -> {
            header = MainHeaderPacket()
            if (!StartCode.Main.equalsCode(startcode)) {
              throw IOException("expected main header found: 0x${startcode.toString(16).uppercase()}")
            }
            header.read(dataInputStream, startcode)
            startcode = dataInputStream.readStartCode()
          }
          StartCode.Stream -> {
            if (!StartCode.Stream.equalsCode(startcode)) {
              throw IOException("expected stream header found: 0x${startcode.toString(16).uppercase()}")
            }
            val streamHeader = StreamHeaderPacket()
            streamHeader.read(dataInputStream, startcode)
            val stream = Stream(header, streamHeader)
            streams.add(stream)
            listener.stream(stream)
            startcode = dataInputStream.readStartCode()
          }
          StartCode.SyncPoint -> {
            Packet().read(dataInputStream, startcode) // Discard for the moment
            startcode = dataInputStream.readStartCode()
          }
          StartCode.Index -> {
            Packet().read(dataInputStream, startcode) // Discard for the moment
            startcode = dataInputStream.readStartCode()
          }
          StartCode.Info -> {
            Packet().read(dataInputStream, startcode) // Discard for the moment
            startcode = dataInputStream.readStartCode()
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
