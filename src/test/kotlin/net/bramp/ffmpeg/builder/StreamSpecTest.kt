package net.bramp.ffmpeg.builder

import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.id
import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.program
import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.stream
import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.tag
import net.bramp.ffmpeg.builder.StreamSpecifier.Companion.usable
import net.bramp.ffmpeg.builder.StreamSpecifierType.Attachment
import net.bramp.ffmpeg.builder.StreamSpecifierType.Audio
import net.bramp.ffmpeg.builder.StreamSpecifierType.Data
import net.bramp.ffmpeg.builder.StreamSpecifierType.PureVideo
import net.bramp.ffmpeg.builder.StreamSpecifierType.Subtitle
import net.bramp.ffmpeg.builder.StreamSpecifierType.Video
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StreamSpecTest {

  @Test
  fun testStreamSpec() {
    assertThat(stream(1).spec(), `is`("1"))
    assertThat(stream(Video).spec(), `is`("v"))

    assertThat(stream(Video, 1).spec(), `is`("v:1"))
    assertThat(stream(PureVideo, 1).spec(), `is`("V:1"))
    assertThat(stream(Audio, 1).spec(), `is`("a:1"))
    assertThat(stream(Subtitle, 1).spec(), `is`("s:1"))
    assertThat(stream(Data, 1).spec(), `is`("d:1"))
    assertThat(stream(Attachment, 1).spec(), `is`("t:1"))

    assertThat(program(1).spec(), `is`("p:1"))
    assertThat(program(1, 2).spec(), `is`("p:1:2"))

    assertThat(id(1).spec(), `is`("i:1"))

    assertThat(tag("key").spec(), `is`("m:key"))
    assertThat(tag("key", "value").spec(), `is`("m:key:value"))
    assertThat(usable().spec(), `is`("u"))
  }
}
