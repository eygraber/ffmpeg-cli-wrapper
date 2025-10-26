package net.bramp.ffmpeg.kotlin

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PreconditionsCheckValidNotEmptyTest(private val input: String) {

  @Test
  fun testUri() {
    Preconditions.checkNotNullEmptyOrBlank(input, "test must not throw exception")
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data(): List<String> = listOf("bob", " hello ")
  }
}
