package net.bramp.ffmpeg.kotlin.probe

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.bramp.ffmpeg.kotlin.serialization.CodecTypeSerializer
import net.bramp.ffmpeg.kotlin.shared.CodecType
import org.apache.commons.lang3.math.Fraction

@Serializable
data class FFmpegStream(
  val index: Int = 0,
  @SerialName("codec_name") val codecName: String? = null,
  @SerialName("codec_long_name") val codecLongName: String? = null,
  val profile: String? = null,
  @SerialName("codec_type") @Serializable(with = CodecTypeSerializer::class) val codecType: CodecType? = null,
  @SerialName("codec_time_base") @Contextual val codecTimeBase: Fraction? = null,
  @SerialName("codec_tag_string") val codecTagString: String? = null,
  @SerialName("codec_tag") val codecTag: String? = null,
  val width: Int = 0,
  val height: Int = 0,
  @SerialName("coded_width") val codedWidth: Int? = null,
  @SerialName("coded_height") val codedHeight: Int? = null,
  @SerialName("has_b_frames") val hasBFrames: Int = 0,
  @SerialName("sample_aspect_ratio") val sampleAspectRatio: String? = null,
  @SerialName("display_aspect_ratio") val displayAspectRatio: String? = null,
  @SerialName("pix_fmt") val pixFmt: String? = null,
  val level: Int = 0,
  @SerialName("color_range") val colorRange: String? = null,
  @SerialName("color_space") val colorSpace: String? = null,
  @SerialName("color_transfer") val colorTransfer: String? = null,
  @SerialName("color_primaries") val colorPrimaries: String? = null,
  @SerialName("chroma_location") val chromaLocation: String? = null,
  @SerialName("field_order") val fieldOrder: String? = null,
  val timecode: String? = null,
  val refs: Int = 0,
  val id: String? = null,
  @SerialName("r_frame_rate") @Contextual val rFrameRate: Fraction? = null,
  @SerialName("avg_frame_rate") @Contextual val avgFrameRate: Fraction? = null,
  @SerialName("time_base") @Contextual val timeBase: Fraction? = null,
  @SerialName("start_pts") val startPts: Long = 0,
  @SerialName("start_time") val startTime: Double? = null,
  @SerialName("duration_ts") val durationTs: Long = 0,
  val duration: Double? = null,
  @SerialName("bit_rate") val bitRate: Long = 0,
  @SerialName("max_bit_rate") val maxBitRate: Long = 0,
  @SerialName("bits_per_raw_sample") val bitsPerRawSample: Int = 0,
  @SerialName("nal_length_size") val nalLengthSize: String? = null,
  @Suppress("NonBooleanPropertyPrefixedWithIs")
  @SerialName("is_avc") val isAvc: String? = null,
  @SerialName("bits_per_sample") val bitsPerSample: Int = 0,
  @SerialName("nb_frames") val nbFrames: Long = 0,
  @SerialName("nb_read_frames") val nbReadFrames: Long? = null,
  @SerialName("nb_read_packets") val nbReadPackets: Long? = null,
  val channels: Int = 0,
  @SerialName("sample_rate") val sampleRate: Int = 0,
  @SerialName("channel_layout") val channelLayout: String? = null,
  @SerialName("sample_fmt") val sampleFmt: String? = null,
  val disposition: FFmpegDisposition? = null,
  val tags: Map<String, String>? = null,
  @SerialName("side_data_list") val sideDataList: List<SideData> = emptyList(),
) {
  @Serializable
  data class SideData(
    @SerialName("side_data_type") val sideDataType: String? = null,
    @SerialName("displaymatrix") val displayMatrix: String? = null,
    val rotation: Int = 0,
  )
}
