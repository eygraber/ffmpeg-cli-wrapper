package net.bramp.ffmpeg.serialization

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.bramp.ffmpeg.probe.FFmpegStream
import org.junit.Test

@Suppress("MultilineRawStringIndentation")
class SideDataSerializerTest {

  private val json = Json { ignoreUnknownKeys = true }

  @Test
  fun testNormalVideo() {
    val jsonString = """
      {
        "side_data_type": "Display Matrix",
        "displaymatrix": "\n00000000:            0      -65536           0\n00000001:        65536           0           0\n00000002:            0           0  1073741824\n",
        "rotation": 90
      }
    """.trimIndent()

    val sideData = json.decodeFromString<FFmpegStream.SideData>(jsonString)

    sideData.sideDataType shouldBe "Display Matrix"
    sideData.displayMatrix shouldBe "\n00000000:            0      -65536           0\n00000001:        65536           0           0\n00000002:            0           0  1073741824\n"
    sideData.rotation shouldBe 90
  }

  @Test
  fun testZeroRotation() {
    val jsonString = """
      {
        "side_data_type": "Display Matrix",
        "displaymatrix": "\n00000000:            0           0           0\n00000001:            0           0           0\n00000002:            0           0  1073741824\n",
        "rotation": 0
      }
    """.trimIndent()

    val sideData = json.decodeFromString<FFmpegStream.SideData>(jsonString)

    sideData.sideDataType shouldBe "Display Matrix"
    sideData.displayMatrix shouldBe "\n00000000:            0           0           0\n00000001:            0           0           0\n00000002:            0           0  1073741824\n"
    sideData.rotation shouldBe 0
  }
}
