package net.bramp.ffmpeg.probe

import com.google.gson.annotations.SerializedName
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import net.bramp.ffmpeg.shared.CodecType
import org.apache.commons.lang3.math.Fraction

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegStream(
  val index: Int = 0,
  @SerializedName("codec_name")
  val codecName: String = "",
  @SerializedName("codec_long_name")
  val codecLongName: String = "",
  val profile: String = "",
  @SerializedName("codec_type")
  val codecType: CodecType? = null,
  @SerializedName("codec_time_base")
  val codecTimeBase: Fraction? = null,
  @SerializedName("codec_tag_string")
  val codecTagString: String = "",
  @SerializedName("codec_tag")
  val codecTag: String = "",
  val width: Int = 0,
  val height: Int = 0,
  @SerializedName("has_bframes")
  val hasBFrames: Int = 0,
  @SerializedName("sample_aspect_ratio")
  val sampleAspectRatio: String? = null, // TODO Change to a Ratio/Fraction object
  @SerializedName("display_aspect_ratio")
  val displayAspectRatio: String? = null,
  @SerializedName("pix_fmt")
  val pixFmt: String? = null,
  val level: Int = 0,
  @SerializedName("chroma_location")
  val chromaLocation: String? = null,
  val refs: Int = 0,
  @Suppress("NonBooleanPropertyPrefixedWithIs")
  @SerializedName("is_avc")
  val isAvc: String? = null,
  @SerializedName("nal_length_size")
  val nalLengthSize: String? = null,
  val id: String = "",
  @SerializedName("r_frame_rate")
  val rFrameRate: Fraction? = null,
  @SerializedName("avg_frame_rate")
  val avgFrameRate: Fraction? = null,
  @SerializedName("time_base")
  val timeBase: Fraction? = null,
  @SerializedName("start_pts")
  val startPts: Long = 0,
  @SerializedName("start_time")
  val startTime: Double = 0.0,
  @SerializedName("duration_ts")
  val durationTs: Long = 0,
  val duration: Double = 0.0,
  @SerializedName("bit_rate")
  val bitRate: Long = 0,
  @SerializedName("max_bit_rate")
  val maxBitRate: Long = 0,
  @SerializedName("bits_per_raw_sample")
  val bitsPerRawSample: Int = 0,
  @SerializedName("bits_per_sample")
  val bitsPerSample: Int = 0,
  @SerializedName("nb_frames")
  val nbFrames: Long = 0,
  @SerializedName("sample_fmt")
  val sampleFmt: String? = null,
  @SerializedName("sample_rate")
  val sampleRate: Int = 0,
  val channels: Int = 0,
  @SerializedName("channel_layout")
  val channelLayout: String? = null,
  val disposition: FFmpegDisposition? = null,
  val tags: Map<String, String>? = null,
  @SerializedName("side_data_list")
  val sideDataList: List<SideData>? = emptyList(),
) {
  data class SideData(
    val sideDataType: String = "",
    val displayMatrix: String = "",
    val rotation: Int = 0,
  )
}
