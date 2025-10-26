package net.bramp.ffmpeg;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class PreconditionsCheckInvalidNotEmptyTest {
  @Parameterized.Parameters(name = "{0}")
  public static List<String> data() {
    return Arrays.asList(null, "", "   ", "\n", " \n ");
  }

  private final String input;

  public PreconditionsCheckInvalidNotEmptyTest(String input) {
    this.input = input;
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUri() {
    Preconditions.checkNotNullEmptyOrBlank(input, "test must throw exception");
  }
}
