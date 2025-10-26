package net.bramp.ffmpeg.kotlin.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FFmpegDisposition(
  val default: Int = 0,
  val dub: Int = 0,
  val original: Int = 0,
  val comment: Int = 0,
  val lyrics: Int = 0,
  val karaoke: Int = 0,
  val forced: Int = 0,
  @SerialName("hearing_impaired") val hearingImpaired: Int = 0,
  @SerialName("visual_impaired") val visualImpaired: Int = 0,
  @SerialName("clean_effects") val cleanEffects: Int = 0,
  @SerialName("attached_pic") val attachedPic: Int = 0,
  @SerialName("timed_thumbnails") val timedThumbnails: Int = 0,
  @SerialName("non_diegetic") val nonDiegetic: Int = 0,
  val captions: Int = 0,
  val descriptions: Int = 0,
  val metadata: Int = 0,
  val dependent: Int = 0,
  @SerialName("still_image") val stillImage: Int = 0,
) {
  fun isDefault(): Boolean = default != 0
  fun isDub(): Boolean = dub != 0
  fun isOriginal(): Boolean = original != 0
  fun isComment(): Boolean = comment != 0
  fun isLyrics(): Boolean = lyrics != 0
  fun isKaraoke(): Boolean = karaoke != 0
  fun isForced(): Boolean = forced != 0
  fun isHearingImpaired(): Boolean = hearingImpaired != 0
  fun isVisualImpaired(): Boolean = visualImpaired != 0
  fun isCleanEffects(): Boolean = cleanEffects != 0
  fun isAttachedPic(): Boolean = attachedPic != 0
  fun isTimedThumbnails(): Boolean = timedThumbnails != 0
  fun isNonDiegetic(): Boolean = nonDiegetic != 0
  fun isCaptions(): Boolean = captions != 0
  fun isDescriptions(): Boolean = descriptions != 0
  fun isMetadata(): Boolean = metadata != 0
  fun isDependent(): Boolean = dependent != 0
  fun isStillImage(): Boolean = stillImage != 0
}
