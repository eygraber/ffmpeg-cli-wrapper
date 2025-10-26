package net.bramp.ffmpeg.kotlin

import java.net.URI

// Changed to an object as it's a final class with private constructor and only static methods
object Preconditions {

  private val rtps: List<String> = listOf("rtsp", "rtp", "rtmp")
  private val udpTcp: List<String> = listOf("udp", "tcp")

  /**
   * Ensures the argument is not null, empty string, or just whitespace.
   *
   * @param arg The argument
   * @param errorMessage The exception message to use if the check fails
   * @return The passed in argument if it is not blank
   */
  @JvmStatic
  fun checkNotNullEmptyOrBlank(arg: String?, errorMessage: Any?): String {
    require(!arg.isNullOrBlank()) {
      errorMessage?.toString() ?: "Argument cannot be null, empty, or blank"
    }

    return arg
  }

  /**
   * Checks if the URI is valid for streaming to.
   *
   * @param uri The URI to check
   * @return The passed in URI if it is valid
   * @throws IllegalArgumentException if the URI is not valid.
   */
  @JvmStatic
  @Throws(IllegalArgumentException::class)
  fun checkValidStream(uri: URI): URI {
    val scheme = uri.scheme
    val lowerScheme = requireNotNull(scheme) { "URI is missing a scheme" }.lowercase()

    when {
      lowerScheme in rtps -> return uri
      lowerScheme in udpTcp -> {
        require(uri.port != -1) { "must set port when using udp or tcp scheme" }
        return uri
      }

      else -> throw IllegalArgumentException("not a valid output URL, must use rtp/tcp/udp scheme. Found: $lowerScheme")
    }
  }
}
