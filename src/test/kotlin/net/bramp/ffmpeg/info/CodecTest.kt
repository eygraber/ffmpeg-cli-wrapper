package net.bramp.ffmpeg.info

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.shared.CodecType
import org.junit.Test

class CodecTest {
  @Test
  fun testCodecConstructor() {
    val c1 = Codec("012v", "Uncompressed 4:2:2 10-bit", "D.VI.S")
    c1.name shouldBe "012v"
    c1.longName shouldBe "Uncompressed 4:2:2 10-bit"
    c1.canDecode shouldBe true
    c1.canEncode shouldBe false
    c1.type shouldBe CodecType.Video
    c1.isIntraFrameOnly shouldBe true
    c1.isLossyCompressionSupported shouldBe false
    c1.isLosslessCompressionSupported shouldBe true

    val c2 = Codec("4xm", "4X Movie", "D.V.L.")
    c2.name shouldBe "4xm"
    c2.longName shouldBe "4X Movie"
    c2.canDecode shouldBe true
    c2.canEncode shouldBe false
    c2.type shouldBe CodecType.Video
    c2.isIntraFrameOnly shouldBe false
    c2.isLossyCompressionSupported shouldBe true
    c2.isLosslessCompressionSupported shouldBe false

    val c3 = Codec("alias_pix", "Alias/Wavefront PIX image", "DEVI.S")
    c3.name shouldBe "alias_pix"
    c3.longName shouldBe "Alias/Wavefront PIX image"
    c3.canDecode shouldBe true
    c3.canEncode shouldBe true
    c3.type shouldBe CodecType.Video
    c3.isIntraFrameOnly shouldBe true
    c3.isLossyCompressionSupported shouldBe false
    c3.isLosslessCompressionSupported shouldBe true

    val c4 = Codec("binkaudio_rdft", "Bink Audio (RDFT)", "D.AIL.")
    c4.type shouldBe CodecType.Audio

    val c6 = Codec("mov_text", "MOV text", "DES...")
    c6.type shouldBe CodecType.Subtitle

    val c7 = Codec("bin_data", "binary data", "..D...")
    c7.type shouldBe CodecType.Data
  }

  @Test
  fun testBadDecodeValue() {
    shouldThrow<IllegalArgumentException> {
      Codec("test", "test", "X.V...")
    }
  }

  @Test
  fun testBadEncodeValue() {
    shouldThrow<IllegalArgumentException> {
      Codec("test", "test", ".XV...")
    }
  }

  @Test
  fun testBadCodecValue() {
    shouldThrow<IllegalArgumentException> {
      Codec("test", "test", "..X...")
    }
  }

  @Test
  fun testBadIntraFrameOnlyValue() {
    shouldThrow<IllegalArgumentException> {
      Codec("test", "test", "..VX..")
    }
  }

  @Test
  fun testBadLossyValue() {
    shouldThrow<IllegalArgumentException> {
      Codec("test", "test", "..V.X.")
    }
  }

  @Test
  fun testBadLosslessValue() {
    shouldThrow<IllegalArgumentException> {
      Codec("test", "test", "..V..X")
    }
  }
}
