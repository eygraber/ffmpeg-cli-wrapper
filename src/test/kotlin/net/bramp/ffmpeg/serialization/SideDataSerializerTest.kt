package net.bramp.ffmpeg.serialization

import kotlinx.serialization.json.Json
import net.bramp.ffmpeg.probe.FFmpegStream
import org.junit.Assert.assertEquals
import org.junit.Test

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

    assertEquals("Display Matrix", sideData.sideDataType)
    assertEquals(
      "\n00000000:            0      -65536           0\n00000001:        65536           0           0\n00000002:            0           0  1073741824\n",
      sideData.displayMatrix
    )
    assertEquals(90, sideData.rotation)
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

    assertEquals("Display Matrix", sideData.sideDataType)
    assertEquals(
      "\n00000000:            0           0           0\n00000001:            0           0           0\n00000002:            0           0  1073741824\n",
      sideData.displayMatrix
    )
    assertEquals(0, sideData.rotation)
  }
}
