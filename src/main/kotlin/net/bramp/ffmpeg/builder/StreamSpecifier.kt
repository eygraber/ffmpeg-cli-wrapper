package net.bramp.ffmpeg.builder

import com.google.common.base.Preconditions

class StreamSpecifier private constructor(val spec: String) {

  companion object {
    @JvmStatic
    fun stream(index: Int): StreamSpecifier = StreamSpecifier(index.toString())

    @JvmStatic
    fun stream(type: StreamSpecifierType): StreamSpecifier {
      Preconditions.checkNotNull(type)
      // Assuming StreamSpecifierType.toString() is overridden or .name is suitable.
      // FFmpeg often expects lowercase for these types of specifiers.
      return StreamSpecifier(type.name.lowercase())
    }

    @JvmStatic
    fun stream(type: StreamSpecifierType, index: Int): StreamSpecifier {
      Preconditions.checkNotNull(type)
      return StreamSpecifier("${type.name.lowercase()}:$index")
    }

    @JvmStatic
    fun program(program_id: Int): StreamSpecifier = StreamSpecifier("p:$program_id")

    @JvmStatic
    fun program(program_id: Int, stream_index: Int): StreamSpecifier =
        StreamSpecifier("p:$program_id:$stream_index")

    @JvmStatic
    fun id(stream_id: Int): StreamSpecifier = StreamSpecifier("i:$stream_id")

    @JvmStatic
    fun tag(key: String): StreamSpecifier {
      // Assumes MetadataSpecifier.kt is converted and checkValidKey is accessible
      return StreamSpecifier("m:${MetadataSpecifier.checkValidKey(key)}")
    }

    @JvmStatic
    fun tag(key: String, value: String): StreamSpecifier {
      MetadataSpecifier.checkValidKey(key) // from companion object of MetadataSpecifier
      Preconditions.checkNotNull(value)
      return StreamSpecifier("m:$key:$value")
    }

    @JvmStatic
    fun usable(): StreamSpecifier = StreamSpecifier("u")
  }
}
