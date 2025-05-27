package net.bramp.ffmpeg.builder

import com.google.common.base.Preconditions
import com.google.errorprone.annotations.Immutable

@Immutable
class MetadataSpecifier private constructor(val spec: String) {

  // Secondary constructors for internal use by companion object factory methods
  private constructor(prefix: String, index: Int) : this("$prefix:$index")
  private constructor(prefix: String, streamSpec: StreamSpecifier) : this("$prefix:${streamSpec.spec()}")

  companion object {
    @JvmStatic
    fun checkValidKey(key: String): String {
      Preconditions.checkNotNull(key, "Key must not be null")
      Preconditions.checkArgument(key.isNotEmpty(), "key must not be empty")
      Preconditions.checkArgument(key.matches("\\w+".toRegex()), "key must only contain letters, numbers or _")
      return key
    }

    @JvmStatic fun global(): MetadataSpecifier = MetadataSpecifier("g")
    @JvmStatic fun chapter(index: Int): MetadataSpecifier = MetadataSpecifier("c", index)
    @JvmStatic fun program(index: Int): MetadataSpecifier = MetadataSpecifier("p", index)
    
    @JvmStatic fun stream(index: Int): MetadataSpecifier =
        MetadataSpecifier("s", StreamSpecifier.stream(index)) // Assumes StreamSpecifier.kt is available
    
    @JvmStatic fun stream(type: StreamSpecifierType): MetadataSpecifier = // Assumes StreamSpecifierType.kt is available
        MetadataSpecifier("s", StreamSpecifier.stream(type))

    @JvmStatic fun stream(stream_type: StreamSpecifierType, stream_index: Int): MetadataSpecifier =
        MetadataSpecifier("s", StreamSpecifier.stream(stream_type, stream_index))

    @JvmStatic fun stream(spec: StreamSpecifier): MetadataSpecifier {
      Preconditions.checkNotNull(spec, "StreamSpecifier must not be null")
      return MetadataSpecifier("s", spec)
    }
  }
}
