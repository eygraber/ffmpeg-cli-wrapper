package net.bramp.ffmpeg.builder

import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.chapter
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.global
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.program
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.stream
import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.id

import io.kotest.matchers.shouldBe
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
