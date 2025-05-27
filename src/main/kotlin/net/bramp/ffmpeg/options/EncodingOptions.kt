package net.bramp.ffmpeg.options

import java.beans.ConstructorProperties

/**
 * Audio, Video and Main encoding options for ffmpeg.
 *
 * @author bramp
 */
data class EncodingOptions @ConstructorProperties("main", "audio", "video") constructor(
  val main: MainEncodingOptions,
  val audio: AudioEncodingOptions,
  val video: VideoEncodingOptions,
)
