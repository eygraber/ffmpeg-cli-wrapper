package net.bramp.ffmpeg.probe

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import net.bramp.ffmpeg.shared.CodecType
import org.apache.commons.lang3.math.Fraction

@SuppressFBWarnings(
  value = ["UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD"],
  justification = "POJO objects where the fields are populated by gson",
)
data class FFmpegStream(
  val index: Int = 0,
  val codec_name: String = "",
  val codec_long_name: String = "",
  val profile: String = "",
  val codec_type: CodecType? = null,
  val codec_time_base: Fraction? = null,
  val codec_tag_string: String = "",
  val codec_tag: String = "",
  val width: Int = 0,
  val height: Int = 0,
  val has_b_frames: Int = 0,
  val sample_aspect_ratio: String? = null, // TODO Change to a Ratio/Fraction object
  val display_aspect_ratio: String? = null,
  val pix_fmt: String? = null,
  val level: Int = 0,
  val chroma_location: String? = null,
  val refs: Int = 0,
  val is_avc: String? = null,
  val nal_length_size: String? = null,
  val id: String = "",
  val r_frame_rate: Fraction? = null,
  val avg_frame_rate: Fraction? = null,
  val time_base: Fraction? = null,
  val start_pts: Long = 0,
  val start_time: Double = 0.0,
  val duration_ts: Long = 0,
  val duration: Double = 0.0,
  val bit_rate: Long = 0,
  val max_bit_rate: Long = 0,
  val bits_per_raw_sample: Int = 0,
  val bits_per_sample: Int = 0,
  val nb_frames: Long = 0,
  val sample_fmt: String? = null,
  val sample_rate: Int = 0,
  val channels: Int = 0,
  val channel_layout: String? = null,
  val disposition: FFmpegDisposition? = null,
  val tags: Map<String, String>? = null,
  val side_data_list: List<SideData>? = emptyList(),
) {
  data class SideData(
    val side_data_type: String = "",
    val displaymatrix: String = "",
    val rotation: Int = 0,
  )
}
