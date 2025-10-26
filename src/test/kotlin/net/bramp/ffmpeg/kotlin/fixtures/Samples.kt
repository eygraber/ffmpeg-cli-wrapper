package net.bramp.ffmpeg.kotlin.fixtures

object Samples {
  // Test sample files (only a handful to keep the repo small)
  const val TEST_PREFIX = "src/test/resources/net/bramp/ffmpeg/kotlin/samples/"

  const val base_big_buck_bunny_720p_1mb = "big_buck_bunny_720p_1mb.mp4"
  const val base_testscreen_jpg = "testscreen.jpg"
  const val base_test_mp3 = "test.mp3"

  const val big_buck_bunny_720p_1mb = TEST_PREFIX + base_big_buck_bunny_720p_1mb
  const val testscreen_jpg = TEST_PREFIX + base_testscreen_jpg
  const val test_mp3 = TEST_PREFIX + base_test_mp3

  private const val book_m4b = "book_with_chapters.m4b"
  const val book_with_chapters = TEST_PREFIX + book_m4b
  private const val base_side_data_list = "side_data_list"
  const val side_data_list = TEST_PREFIX + base_side_data_list
  private const val base_disposition_all_true = "disposition_all_true"
  const val disposition_all_true = TEST_PREFIX + base_disposition_all_true

  // We don't have the following files
  const val FAKE_PREFIX = "fake/"

  const val always_on_my_mind = FAKE_PREFIX + "Always On My Mind [Program Only] - Adelen.mp4"
  const val start_pts_test = FAKE_PREFIX + "start_pts_test_1mb.ts"
  const val divide_by_zero = FAKE_PREFIX + "Divide By Zero.mp4"
  const val big_buck_bunny_720p_1mb_with_packets =
    FAKE_PREFIX + "big_buck_bunny_720p_1mb_packets.mp4"
  const val big_buck_bunny_720p_1mb_with_frames =
    FAKE_PREFIX + "big_buck_bunny_720p_1mb_frames.mp4"
  const val big_buck_bunny_720p_1mb_with_packets_and_frames =
    FAKE_PREFIX + "big_buck_bunny_720p_1mb_packets_and_frames.mp4"
  const val chapters_with_long_id = FAKE_PREFIX + "chapters_with_long_id.m4b"

  // TODO Change to a temp directory
  // TODO Generate random names, so we can run tests concurrently
  const val output_mp4 = "output.mp4"
}
