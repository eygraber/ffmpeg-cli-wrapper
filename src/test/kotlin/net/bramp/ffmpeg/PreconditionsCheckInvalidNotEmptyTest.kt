package net.bramp.ffmpeg

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PreconditionsCheckInvalidNotEmptyTest(private val input: String?) {

  @Test(expected = IllegalArgumentException::class)
  fun testUri() {
    Preconditions.checkNotNullEmptyOrBlank(input, "test must throw exception")
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data(): List<String?> = listOf(null, "", "   ", "\n", " \n ")
  }
}
