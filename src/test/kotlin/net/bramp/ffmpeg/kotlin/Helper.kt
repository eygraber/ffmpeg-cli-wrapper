package net.bramp.ffmpeg.kotlin

import java.io.InputStream
import java.io.SequenceInputStream
import java.util.Collections

/** Random test helper methods. */
object Helper {
  /**
   * Simple wrapper around "new SequenceInputStream", so the user doesn't have to deal with the
   * horribly dated Enumeration type.
   */
  fun sequenceInputStream(input: Iterable<InputStream>): InputStream =
    SequenceInputStream(Collections.enumeration(input.toList()))

  fun loadResource(name: String): InputStream =
    Helper::class.java.getResourceAsStream("fixtures/$name")
      ?: throw IllegalArgumentException("Resource not found: $name")

  /** Loads all resources, and returns one stream containing them all. */
  fun combineResource(names: List<String>): InputStream =
    sequenceInputStream(names.map { loadResource(it) })

  fun <T> subList(input: List<T>, start: Int): List<T> = input.subList(start, input.size)
}
