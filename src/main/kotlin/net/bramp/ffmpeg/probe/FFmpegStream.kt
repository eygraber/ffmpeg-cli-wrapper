package net.bramp.ffmpeg.probe

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.bramp.ffmpeg.shared.CodecType
import net.bramp.ffmpeg.serde.CodecTypeSerializer
import org.apache.commons.lang3.math.Fraction

@Serializable
data class FFmpegStream(
  var index: Int = 0,
  @SerialName("codec_name") var codecName: String? = null,
  @SerialName("codec_long_name") var codecLongName: String? = null,
  var profile: String? = null,
  @SerialName("codec_type") @Serializable(with = CodecTypeSerializer::class) var codecType: CodecType? = null,
  @SerialName("codec_time_base") @Contextual var codecTimeBase: Fraction? = null,
  @SerialName("codec_tag_string") var codecTagString: String? = null,
  @SerialName("codec_tag") var codecTag: String? = null,
  var width: Int = 0,
  var height: Int = 0,
  @SerialName("coded_width") var codedWidth: Int? = null,
  @SerialName("coded_height") var codedHeight: Int? = null,
  @SerialName("has_b_frames") var hasBFrames: Int = 0,
  @SerialName("sample_aspect_ratio") var sampleAspectRatio: String? = null,
  @SerialName("display_aspect_ratio") var displayAspectRatio: String? = null,
  @SerialName("pix_fmt") var pixFmt: String? = null,
  var level: Int = 0,
  @SerialName("color_range") var colorRange: String? = null,
  @SerialName("color_space") var colorSpace: String? = null,
  @SerialName("color_transfer") var colorTransfer: String? = null,
  @SerialName("color_primaries") var colorPrimaries: String? = null,
  @SerialName("chroma_location") var chromaLocation: String? = null,
  @SerialName("field_order") var fieldOrder: String? = null,
  var timecode: String? = null,
  var refs: Int = 0,
  var id: String? = null,
  @SerialName("r_frame_rate") @Contextual var rFrameRate: Fraction? = null,
  @SerialName("avg_frame_rate") @Contextual var avgFrameRate: Fraction? = null,
  @SerialName("time_base") @Contextual var timeBase: Fraction? = null,
  @SerialName("start_pts") var startPts: Long = 0,
  @SerialName("start_time") var startTime: Double? = null,
  @SerialName("duration_ts") var durationTs: Long = 0,
  var duration: Double? = null,
  @SerialName("bit_rate") var bitRate: Long = 0,
  @SerialName("max_bit_rate") var maxBitRate: Long = 0,
  @SerialName("bits_per_raw_sample") var bitsPerRawSample: Int = 0,
  @SerialName("nal_length_size") var nalLengthSize: String? = null,
  @SerialName("is_avc") var isAvc: String? = null,
  @SerialName("bits_per_sample") var bitsPerSample: Int = 0,
  @SerialName("nb_frames") var nbFrames: Long = 0,
  @SerialName("nb_read_frames") var nbReadFrames: Long? = null,
  @SerialName("nb_read_packets") var nbReadPackets: Long? = null,
  var channels: Int = 0,
  @SerialName("sample_rate") var sampleRate: Int = 0,
  @SerialName("channel_layout") var channelLayout: String? = null,
  @SerialName("sample_fmt") var sampleFmt: String? = null,
  var disposition: FFmpegDisposition? = null,
  var tags: Map<String, String>? = null,
  @SerialName("side_data_list") var sideDataList: List<SideData> = emptyList(),
) {
    @Serializable
    data class SideData(
      @SerialName("side_data_type") var sideDataType: String? = null,
      @SerialName("displaymatrix") var displayMatrix: String? = null,
      var rotation: Int = 0,
    )
}
