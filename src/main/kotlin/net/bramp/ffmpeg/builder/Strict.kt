package net.bramp.ffmpeg.builder

enum class Strict {
  VERY, // strictly conform to an older more strict version of the specifications or reference software
  STRICT, // strictly conform to all the things in the specificiations no matter what consequences
  NORMAL, // normal
  UNOFFICIAL, // allow unofficial extensions
  EXPERIMENTAL;

  override fun toString(): String {
    // ffmpeg command line requires these options in lower case
    return name.lowercase()
  }
}
