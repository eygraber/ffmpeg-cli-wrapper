package net.bramp.ffmpeg.builder;

import static net.bramp.ffmpeg.builder.StreamSpecifier.*;
import static net.bramp.ffmpeg.builder.StreamSpecifierType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class StreamSpecTest {

  @Test
  public void testStreamSpec() {
    assertThat(StreamSpecifier.Companion.stream(1).spec(), is("1"));
    assertThat(StreamSpecifier.Companion.stream(Video).spec(), is("v"));

    assertThat(StreamSpecifier.Companion.stream(Video, 1).spec(), is("v:1"));
    assertThat(StreamSpecifier.Companion.stream(PureVideo, 1).spec(), is("V:1"));
    assertThat(StreamSpecifier.Companion.stream(Audio, 1).spec(), is("a:1"));
    assertThat(StreamSpecifier.Companion.stream(Subtitle, 1).spec(), is("s:1"));
    assertThat(StreamSpecifier.Companion.stream(Data, 1).spec(), is("d:1"));
    assertThat(StreamSpecifier.Companion.stream(Attachment, 1).spec(), is("t:1"));

    assertThat(StreamSpecifier.Companion.program(1).spec(), is("p:1"));
    assertThat(StreamSpecifier.Companion.program(1, 2).spec(), is("p:1:2"));

    assertThat(StreamSpecifier.Companion.id(1).spec(), is("i:1"));

    assertThat(StreamSpecifier.Companion.tag("key").spec(), is("m:key"));
    assertThat(StreamSpecifier.Companion.tag("key", "value").spec(), is("m:key:value"));
    assertThat(StreamSpecifier.Companion.usable().spec(), is("u"));
  }
}
