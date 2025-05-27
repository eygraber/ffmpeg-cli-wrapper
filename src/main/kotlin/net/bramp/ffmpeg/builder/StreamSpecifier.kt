package net.bramp.ffmpeg.builder

import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.checkValidKey

/** [Stream specifier](https://ffmpeg.org/ffmpeg.html#Stream-specifiers)  */
class StreamSpecifier private constructor(private val spec: String) {
  fun spec(): String = spec

  companion object {
    /**
     * Matches the stream with this index.
     *
     * @param index The stream index
     * @return A new StreamSpecifier
     */
    fun stream(index: Int): StreamSpecifier = StreamSpecifier(index.toString())

    /**
     * Matches all streams of this type.
     *
     * @param type The stream type
     * @return A new StreamSpecifier
     */
    fun stream(type: StreamSpecifierType): StreamSpecifier = StreamSpecifier(type.toString())

    /**
     * Matches the stream number stream_index of this type.
     *
     * @param type The stream type
     * @param index The stream index
     * @return A new StreamSpecifier
     */
    fun stream(type: StreamSpecifierType, index: Int): StreamSpecifier = StreamSpecifier("$type:$index")

    /**
     * Matches all streams in the program.
     *
     * @param programId The program id
     * @return A new StreamSpecifier
     */
    fun program(programId: Int): StreamSpecifier = StreamSpecifier("p:$programId")

    /**
     * Matches the stream with number stream_index in the program with the id program_id.
     *
     * @param programId The program id
     * @param streamIndex The stream index
     * @return A new StreamSpecifier
     */
    fun program(programId: Int, streamIndex: Int): StreamSpecifier = StreamSpecifier("p:$programId:$streamIndex")

    /**
     * Match the stream by stream id (e.g. PID in MPEG-TS container).
     *
     * @param streamId The stream id
     * @return A new StreamSpecifier
     */
    fun id(streamId: Int): StreamSpecifier = StreamSpecifier("i:$streamId")

    /**
     * Matches all streams with the given metadata tag.
     *
     * @param key The metadata tag
     * @return A new StreamSpecifier
     */
    fun tag(key: String): StreamSpecifier = StreamSpecifier("m:" + checkValidKey(key))

    /**
     * Matches streams with the metadata tag key having the specified value.
     *
     * @param key The metadata tag
     * @param value The metatdata's value
     * @return A new StreamSpecifier
     */
    fun tag(key: String, value: String): StreamSpecifier {
      checkValidKey(key)
      return StreamSpecifier("m:$key:$value")
    }

    /**
     * Matches streams with usable configuration, the codec must be defined and the essential
     * information such as video dimension or audio sample rate must be present.
     *
     * @return A new StreamSpecifier
     */
    fun usable(): StreamSpecifier = StreamSpecifier("u")
  }
}
