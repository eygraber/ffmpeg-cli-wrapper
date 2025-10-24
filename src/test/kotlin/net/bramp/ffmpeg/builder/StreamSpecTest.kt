package net.bramp.ffmpeg.builder

import io.kotest.matchers.shouldBe
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
import org.junit.Test

class StreamSpecTest {

  @Test
  fun testStreamSpec() {
    stream(1).spec() shouldBe "1"
    stream(Video).spec() shouldBe "v"

    stream(Video, 1).spec() shouldBe "v:1"
    stream(PureVideo, 1).spec() shouldBe "V:1"
    stream(Audio, 1).spec() shouldBe "a:1"
    stream(Subtitle, 1).spec() shouldBe "s:1"
    stream(Data, 1).spec() shouldBe "d:1"
    stream(Attachment, 1).spec() shouldBe "t:1"

    program(1).spec() shouldBe "p:1"
    program(1, 2).spec() shouldBe "p:1:2"

    id(1).spec() shouldBe "i:1"

    tag("key").spec() shouldBe "m:key"
    tag("key", "value").spec() shouldBe "m:key:value"
    usable().spec() shouldBe "u"
  }
}
