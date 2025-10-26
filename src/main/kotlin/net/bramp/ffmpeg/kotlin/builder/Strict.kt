package net.bramp.ffmpeg.kotlin.builder

enum class Strict {
  /**
   * strictly conform to an older more strict version of the specifications or reference software
   */
  Very,

  /**
   * strictly conform to all the things in the specifications no matter what consequences
   */
  Strict,
  Normal,

  /**
   * allow unofficial extensions
   */
  Unofficial,
  Experimental,
  ;

  override fun toString(): String = name.lowercase()
}
