package net.bramp.ffmpeg.kotlin.builder

import io.kotest.matchers.shouldBe
import net.bramp.ffmpeg.kotlin.builder.MetadataSpecifier.Companion.chapter
import net.bramp.ffmpeg.kotlin.builder.MetadataSpecifier.Companion.global
import net.bramp.ffmpeg.kotlin.builder.MetadataSpecifier.Companion.program
import net.bramp.ffmpeg.kotlin.builder.MetadataSpecifier.Companion.stream
import net.bramp.ffmpeg.kotlin.builder.StreamSpecifier.Companion.id
import org.junit.Test

class MetadataSpecTest {

  @Test
  fun testMetaSpec() {
    global().spec shouldBe "g"
    chapter(1).spec shouldBe "c:1"
    program(1).spec shouldBe "p:1"
    stream(1).spec shouldBe "s:1"
    stream(id(1)).spec shouldBe "s:i:1"
  }
}
