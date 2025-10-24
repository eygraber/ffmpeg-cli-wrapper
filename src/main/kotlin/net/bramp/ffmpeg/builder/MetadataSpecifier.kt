package net.bramp.ffmpeg.builder

/**
 * Metadata spec, as described in the "map_metadata" section of [Main options](https://www.ffmpeg.org/ffmpeg-all.html#Main-options)
 */
class MetadataSpecifier private constructor(val spec: String) {
  private constructor(prefix: String, index: Int) : this(prefix + ":" + index)
  private constructor(prefix: String, spec: StreamSpecifier) : this(prefix + ":" + spec.spec())

  companion object {
    fun checkValidKey(key: String): String {
      require(key.isNotEmpty()) { "key must not be empty" }
      require(key.matches("\\w+".toRegex())) { "key must only contain letters, numbers or _" }
      return key
    }

    fun global(): MetadataSpecifier = MetadataSpecifier("g")

    fun chapter(index: Int): MetadataSpecifier = MetadataSpecifier("c", index)

    fun program(index: Int): MetadataSpecifier = MetadataSpecifier("p", index)

    fun stream(index: Int): MetadataSpecifier = MetadataSpecifier("s", StreamSpecifier.stream(index))

    fun stream(type: StreamSpecifierType): MetadataSpecifier = MetadataSpecifier("s", StreamSpecifier.stream(type))

    fun stream(streamType: StreamSpecifierType, streamIndex: Int): MetadataSpecifier = MetadataSpecifier(
      "s",
      StreamSpecifier.stream(streamType, streamIndex),
    )

    fun stream(spec: StreamSpecifier): MetadataSpecifier = MetadataSpecifier("s", spec)
  }
}
