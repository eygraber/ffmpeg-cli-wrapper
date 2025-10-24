package net.bramp.ffmpeg.probe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FFmpegDisposition(
  var default: Int = 0,
  var dub: Int = 0,
  var original: Int = 0,
  var comment: Int = 0,
  var lyrics: Int = 0,
  var karaoke: Int = 0,
  var forced: Int = 0,
  @SerialName("hearing_impaired") var hearingImpaired: Int = 0,
  @SerialName("visual_impaired") var visualImpaired: Int = 0,
  @SerialName("clean_effects") var cleanEffects: Int = 0,
  @SerialName("attached_pic") var attachedPic: Int = 0,
  @SerialName("timed_thumbnails") var timedThumbnails: Int = 0,
  @SerialName("non_diegetic") var nonDiegetic: Int = 0,
  var captions: Int = 0,
  var descriptions: Int = 0,
  var metadata: Int = 0,
  var dependent: Int = 0,
  @SerialName("still_image") var stillImage: Int = 0,
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
