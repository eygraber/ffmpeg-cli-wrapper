package net.bramp.ffmpeg.kotlin

import io.kotest.assertions.throwables.shouldThrow
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.net.URI

@RunWith(Parameterized::class)
class PreconditionsCheckInvalidStreamTest(url: String) {

  private val uri: URI = URI.create(url)

  @Test
  fun testUri() {
    shouldThrow<IllegalArgumentException> {
      Preconditions.checkValidStream(uri)
    }
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data(): List<String> = listOf(
      // Illegal schemes
      "http://www.example.com/",
      "https://live.twitch.tv/app/live_",
      "ftp://236.0.0.1:2000",

      // Missing ports
      "udp://10.1.0.102/",
      "tcp://127.0.0.1/",
    )
  }
}
