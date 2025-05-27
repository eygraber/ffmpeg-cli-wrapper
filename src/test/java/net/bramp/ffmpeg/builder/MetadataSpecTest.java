package net.bramp.ffmpeg.builder;

import static net.bramp.ffmpeg.builder.MetadataSpecifier.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class MetadataSpecTest {

  @Test
  public void testMetaSpec() {
    assertThat(MetadataSpecifier.Companion.global().getSpec(), is("g"));
    assertThat(MetadataSpecifier.Companion.chapter(1).getSpec(), is("c:1"));
    assertThat(MetadataSpecifier.Companion.program(1).getSpec(), is("p:1"));
    assertThat(MetadataSpecifier.Companion.stream(1).getSpec(), is("s:1"));
    assertThat(MetadataSpecifier.Companion.stream(StreamSpecifier.Companion.id(1)).getSpec(), is("s:i:1"));
  }
}
