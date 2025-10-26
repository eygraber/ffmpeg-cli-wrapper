package net.bramp.ffmpeg.kotlin.progress

/** Test class to keep a record of all progresses. */
class RecordingProgressListener : ProgressListener {
  val progresses = mutableListOf<Progress>()

  override fun progress(progress: Progress) {
    progresses.add(progress)
  }

  fun reset() {
    progresses.clear()
  }
}
