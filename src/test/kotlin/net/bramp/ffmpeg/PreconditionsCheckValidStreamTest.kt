package net.bramp.ffmpeg

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.net.URI

@RunWith(Parameterized::class)
class PreconditionsCheckValidStreamTest(url: String) {

  private val uri: URI = URI.create(url)

  @Test
  fun testUri() {
    Preconditions.checkValidStream(uri)
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data(): List<String> = listOf(
      "udp://10.1.0.102:1234",
      "tcp://127.0.0.1:2000",
      "udp://236.0.0.1:2000",
      "rtmp://live.twitch.tv/app/live_",
      "rtmp:///live/myStream.sdp",
      "rtp://127.0.0.1:1234",
      "rtsp://localhost:8888/live.sdp",
      "rtsp://localhost:8888/live.sdp?tcp",

      // Some others
      "UDP://10.1.0.102:1234"
    )
  }
}
