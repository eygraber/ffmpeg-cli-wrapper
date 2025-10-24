package net.bramp.ffmpeg.lang

import net.bramp.ffmpeg.Helper
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

class NewProcessAnswer(
  private val resource: String,
  private val errResource: String? = null,
) : Answer<Process> {
  override fun answer(invocation: InvocationOnMock): Process =
    if(errResource == null) {
      MockProcess(Helper.loadResource(resource))
    }
    else {
      MockProcess(null, Helper.loadResource(resource), Helper.loadResource(errResource))
    }
}
