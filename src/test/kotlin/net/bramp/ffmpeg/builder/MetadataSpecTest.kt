package net.bramp.ffmpeg.builder

import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.chapter
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.global
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.program
import net.bramp.ffmpeg.builder.MetadataSpecifier.Companion.stream
import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.id
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MetadataSpecTest {

  @Test
  fun testMetaSpec() {
    assertThat(global().spec, `is`("g"))
    assertThat(chapter(1).spec, `is`("c:1"))
    assertThat(program(1).spec, `is`("p:1"))
    assertThat(stream(1).spec, `is`("s:1"))
    assertThat(stream(id(1)).spec, `is`("s:i:1"))
  }
}
