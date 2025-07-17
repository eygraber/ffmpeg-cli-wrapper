package net.bramp.ffmpeg.builder

enum class StreamSpecifierType(private val prefix: String) {
  /** Video  */
  Video("v"),

  /** Video streams which are not attached pictures, video thumbnails or cover arts.  */
  PureVideo("V"),

  /** Audio  */
  Audio("a"),

  /** Subtitles  */
  Subtitle("s"),

  /** Data  */
  Data("d"),

  /** Attachment  */
  Attachment("t"),
  ;

  override fun toString(): String = prefix
}
