package net.bramp.ffmpeg.probe

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import net.bramp.ffmpeg.adapter.BooleanTypeAdapter

/** Represents the AV_DISPOSITION_* fields  */
@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegDisposition(
  @SerializedName("default") @JsonAdapter(BooleanTypeAdapter::class) val isDefault: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isDub: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isOriginal: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isComment: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isLyrics: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isKaraoke: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isForced: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isHearingImpaired: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isVisualImpaired: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isCleanEffects: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isAttachedPic: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isTimedThumbnails: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isNonDiegetic: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isCaptions: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isDescriptions: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isMetadata: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isDependent: Boolean = false,
  @JsonAdapter(BooleanTypeAdapter::class) val isStillImage: Boolean = false,
)
