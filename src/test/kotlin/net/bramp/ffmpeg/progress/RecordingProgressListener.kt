package net.bramp.ffmpeg.progress

/** Test class to keep a record of all progresses. */
class RecordingProgressListener : ProgressListener {
  val progresses = mutableListOf<Progress>()

  override fun progress(p: Progress) {
    progresses.add(p)
  }

  fun reset() {
    progresses.clear()
  }
}
