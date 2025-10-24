package net.bramp.ffmpeg.info

import net.bramp.ffmpeg.shared.CodecType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class CodecTest {
  @Test
  fun testCodecConstructor() {
    val c1 = Codec("012v", "Uncompressed 4:2:2 10-bit", "D.VI.S")
    assertThat(c1.name, `is`("012v"))
    assertThat(c1.longName, `is`("Uncompressed 4:2:2 10-bit"))
    assertThat(c1.canDecode, `is`(true))
    assertThat(c1.canEncode, `is`(false))
    assertThat(c1.type, `is`(CodecType.Video))
    assertThat(c1.isIntraFrameOnly, `is`(true))
    assertThat(c1.isLossyCompressionSupported, `is`(false))
    assertThat(c1.isLosslessCompressionSupported, `is`(true))

    val c2 = Codec("4xm", "4X Movie", "D.V.L.")
    assertThat(c2.name, `is`("4xm"))
    assertThat(c2.longName, `is`("4X Movie"))
    assertThat(c2.canDecode, `is`(true))
    assertThat(c2.canEncode, `is`(false))
    assertThat(c2.type, `is`(CodecType.Video))
    assertThat(c2.isIntraFrameOnly, `is`(false))
    assertThat(c2.isLossyCompressionSupported, `is`(true))
    assertThat(c2.isLosslessCompressionSupported, `is`(false))

    val c3 = Codec("alias_pix", "Alias/Wavefront PIX image", "DEVI.S")
    assertThat(c3.name, `is`("alias_pix"))
    assertThat(c3.longName, `is`("Alias/Wavefront PIX image"))
    assertThat(c3.canDecode, `is`(true))
    assertThat(c3.canEncode, `is`(true))
    assertThat(c3.type, `is`(CodecType.Video))
    assertThat(c3.isIntraFrameOnly, `is`(true))
    assertThat(c3.isLossyCompressionSupported, `is`(false))
    assertThat(c3.isLosslessCompressionSupported, `is`(true))

    val c4 = Codec("binkaudio_rdft", "Bink Audio (RDFT)", "D.AIL.")
    assertThat(c4.type, `is`(CodecType.Audio))

    val c6 = Codec("mov_text", "MOV text", "DES...")
    assertThat(c6.type, `is`(CodecType.Subtitle))

    val c7 = Codec("bin_data", "binary data", "..D...")
    assertThat(c7.type, `is`(CodecType.Data))
  }

  @Test(expected = IllegalArgumentException::class)
  fun testBadDecodeValue() {
    Codec("test", "test", "X.V...")
  }

  @Test(expected = IllegalArgumentException::class)
  fun testBadEncodeValue() {
    Codec("test", "test", ".XV...")
  }

  @Test(expected = IllegalArgumentException::class)
  fun testBadCodecValue() {
    Codec("test", "test", "..X...")
  }

  @Test(expected = IllegalArgumentException::class)
  fun testBadIntraFrameOnlyValue() {
    Codec("test", "test", "..VX..")
  }

  @Test(expected = IllegalArgumentException::class)
  fun testBadLossyValue() {
    Codec("test", "test", "..V.X.")
  }

  @Test(expected = IllegalArgumentException::class)
  fun testBadLosslessValue() {
    Codec("test", "test", "..V..X")
  }
}
