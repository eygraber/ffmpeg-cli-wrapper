package net.bramp.ffmpeg.kotlin.builder

/**
 * The available codecs may vary depending on the version of FFmpeg. <br></br>
 * you can get a list of available codecs through use [net.bramp.ffmpeg.kotlin.FFmpeg.codecs].
 *
 * @see net.bramp.ffmpeg.kotlin.FFmpeg.codecs
 * @author van1164
 */
object VideoCodec {
  /** Uncompressed 4:2:2 10-bit  */
  const val V = "012v"

  /** 4X Movie  */
  const val XM = "4xm"

  /** QuickTime 8BPS video  */
  const val BPS = "8bps"

  /** Multicolor charset for Commodore 64 (encoders: a64multi)  */
  const val A64_MULTI = "a64_multi"

  /** Multicolor charset for Commodore 64, extended with 5th color (colram) (encoders: a64multi5)  */
  const val A64_MULTI5 = "a64_multi5"

  /** Autodesk RLE  */
  const val AASC = "aasc"

  /** Amuse Graphics Movie  */
  const val AGM = "agm"

  /** Apple Intermediate Codec  */
  const val AIC = "aic"

  /** Alias/Wavefront PIX image  */
  const val ALIAS_PIX = "alias_pix"

  /** AMV Video  */
  const val AMV = "amv"

  /** Deluxe Paint Animation  */
  const val ANM = "anm"

  /** ASCII/ANSI art  */
  const val ANSI = "ansi"

  /** APNG (Animated Portable Network Graphics) image  */
  const val APNG = "apng"

  /** Gryphon's Anim Compressor  */
  const val ARBC = "arbc"

  /** Argonaut Games Video  */
  const val ARGO = "argo"

  /** ASUS V1  */
  const val ASV1 = "asv1"

  /** ASUS V2  */
  const val ASV2 = "asv2"

  /** Auravision AURA  */
  const val AURA = "aura"

  /** Auravision Aura 2  */
  const val AURA2 = "aura2"

  /**
   * Alliance for Open Media AV1 (decoders: libaom-av1 av1 av1_cuvid av1_qsv) (encoders: libaom-av1
   * av1_nvenc av1_qsv av1_amf)
   */
  const val AV1 = "av1"

  /** Avid AVI Codec  */
  const val AVRN = "avrn"

  /** Avid 1:1 10-bit RGB Packer  */
  const val AVRP = "avrp"

  /** AVS (Audio Video Standard) video  */
  const val AVS = "avs"

  /** AVS2-P2/IEEE1857.4  */
  const val AVS2 = "avs2"

  /** AVS3-P2/IEEE1857.10  */
  const val AVS3 = "avs3"

  /** Avid Meridien Uncompressed  */
  const val AVUI = "avui"

  /** Uncompressed packed MS 4:4:4:4  */
  const val AYUV = "ayuv"

  /** Bethesda VID video  */
  const val BETHSOFTVID = "bethsoftvid"

  /** Brute Force &amp; Ignorance  */
  const val BFI = "bfi"

  /** Bink video  */
  const val BINKVIDEO = "binkvideo"

  /** Binary text  */
  const val BINTEXT = "bintext"

  /** Bitpacked  */
  const val BITPACKED = "bitpacked"

  /** BMP (Windows and OS/2 bitmap)  */
  const val BMP = "bmp"

  /** Discworld II BMV video  */
  const val BMV_VIDEO = "bmv_video"

  /** BRender PIX image  */
  const val BRENDER_PIX = "brender_pix"

  /** Interplay C93  */
  const val C93 = "c93"

  /** Chinese AVS (Audio Video Standard) (AVS1-P2, JiZhun profile)  */
  const val CAVS = "cavs"

  /** CD Graphics video  */
  const val CDGRAPHICS = "cdgraphics"

  /** CDToons video  */
  const val CDTOONS = "cdtoons"

  /** Commodore CDXL video  */
  const val CDXL = "cdxl"

  /** GoPro CineForm HD  */
  const val CFHD = "cfhd"

  /** Cinepak  */
  const val CINEPAK = "cinepak"

  /** Iterated Systems ClearVideo  */
  const val CLEARVIDEO = "clearvideo"

  /** Cirrus Logic AccuPak  */
  const val CLJR = "cljr"

  /** Canopus Lossless Codec  */
  const val CLLC = "cllc"

  /** Electronic Arts CMV video (decoders: eacmv)  */
  const val CMV = "cmv"

  /** CPiA video format  */
  const val CPIA = "cpia"

  /** Cintel RAW  */
  const val CRI = "cri"

  /** CamStudio (decoders: camstudio)  */
  const val CSCD = "cscd"

  /** Creative YUV (CYUV)  */
  const val CYUV = "cyuv"

  /** Daala  */
  const val DAALA = "daala"

  /** DirectDraw Surface image decoder  */
  const val DDS = "dds"

  /** Chronomaster DFA  */
  const val DFA = "dfa"

  /** Dirac (encoders: vc2)  */
  const val DIRAC = "dirac"

  /** VC3/DNxHD  */
  const val DNXHD = "dnxhd"

  /** DPX (Digital Picture Exchange) image  */
  const val DPX = "dpx"

  /** Delphine Software International CIN video  */
  const val DSICINVIDEO = "dsicinvideo"

  /** DV (Digital Video)  */
  const val DVVIDEO = "dvvideo"

  /** Feeble Files/ScummVM DXA  */
  const val DXA = "dxa"

  /** Dxtory  */
  const val DXTORY = "dxtory"

  /** Resolume DXV  */
  const val DXV = "dxv"

  /** Escape 124  */
  const val ESCAPE124 = "escape124"

  /** Escape 130  */
  const val ESCAPE130 = "escape130"

  /** MPEG-5 EVC (Essential Video Coding)  */
  const val EVC = "evc"

  /** OpenEXR image  */
  const val EXR = "exr"

  /** FFmpeg video codec #1  */
  const val FFV1 = "ffv1"

  /** Huffyuv FFmpeg variant  */
  const val FFVHUFF = "ffvhuff"

  /** Mirillis FIC  */
  const val FIC = "fic"

  /** FITS (Flexible Image Transport System)  */
  const val FITS = "fits"

  /** Flash Screen Video v1  */
  const val FLASHSV = "flashsv"

  /** Flash Screen Video v2  */
  const val FLASHSV2 = "flashsv2"

  /** Autodesk Animator Flic video  */
  const val FLIC = "flic"

  /** FLV / Sorenson Spark / Sorenson H.263 (Flash Video) (decoders: flv) (encoders: flv)  */
  const val FLV1 = "flv1"

  /** FM Screen Capture Codec  */
  const val FMVC = "fmvc"

  /** Fraps  */
  const val FRAPS = "fraps"

  /** Forward Uncompressed  */
  const val FRWU = "frwu"

  /** Go2Meeting  */
  const val G2M = "g2m"

  /** Gremlin Digital Video  */
  const val GDV = "gdv"

  /** GEM Raster image  */
  const val GEM = "gem"

  /** CompuServe GIF (Graphics Interchange Format)  */
  const val GIF = "gif"

  /** H.261  */
  const val H261 = "h261"

  /** H.263 / H.263-1996, H.263+ / H.263-1998 / H.263 version 2  */
  const val H263 = "h263"

  /** Intel H.263  */
  const val H263I = "h263i"

  /** H.263+ / H.263-1998 / H.263 version 2  */
  const val H263P = "h263p"

  /**
   * H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10 (decoders: h264 h264_qsv h264_cuvid) (encoders:
   * libx264 libx264rgb h264_amf h264_mf h264_nvenc h264_qsv)
   */
  const val H264 = "h264"

  /** Vidvox Hap  */
  const val HAP = "hap"

  /** HDR (Radiance RGBE format) image  */
  const val HDR = "hdr"

  /**
   * H.265 / HEVC (High Efficiency Video Coding) (decoders: hevc hevc_qsv hevc_cuvid) (encoders:
   * libx265 hevc_amf hevc_mf hevc_nvenc hevc_qsv)
   */
  const val HEVC = "hevc"

  /** HNM 4 video  */
  const val HNM4VIDEO = "hnm4video"

  /** Canopus HQ/HQA  */
  const val HQ_HQA = "hq_hqa"

  /** Canopus HQX  */
  const val HQX = "hqx"

  /** HuffYUV  */
  const val HUFFYUV = "huffyuv"

  /** HuffYUV MT  */
  const val HYMT = "hymt"

  /** id Quake II CIN video (decoders: idcinvideo)  */
  const val IDCIN = "idcin"

  /** iCEDraw text  */
  const val IDF = "idf"

  /** IFF ACBM/ANIM/DEEP/ILBM/PBM/RGB8/RGBN (decoders: iff)  */
  const val IFF_ILBM = "iff_ilbm"

  /** Infinity IMM4  */
  const val IMM4 = "imm4"

  /** Infinity IMM5  */
  const val IMM5 = "imm5"

  /** Intel Indeo 2  */
  const val INDEO2 = "indeo2"

  /** Intel Indeo 3  */
  const val INDEO3 = "indeo3"

  /** Intel Indeo Video Interactive 4  */
  const val INDEO4 = "indeo4"

  /** Intel Indeo Video Interactive 5  */
  const val INDEO5 = "indeo5"

  /** Interplay MVE video  */
  const val INTERPLAYVIDEO = "interplayvideo"

  /** IPU Video  */
  const val IPU = "ipu"

  /** JPEG 2000 (encoders: jpeg2000 libopenjpeg)  */
  const val JPEG2000 = "jpeg2000"

  /** JPEG-LS  */
  const val JPEGLS = "jpegls"

  /** JPEG XL  */
  const val JPEGXL = "jpegxl"

  /** Bitmap Brothers JV video  */
  const val JV = "jv"

  /** Kega Game Video  */
  const val KGV1 = "kgv1"

  /** Karl Morton's video codec  */
  const val KMVC = "kmvc"

  /** Lagarith lossless  */
  const val LAGARITH = "lagarith"

  /** Lossless JPEG  */
  const val LJPEG = "ljpeg"

  /** LOCO  */
  const val LOCO = "loco"

  /** LEAD Screen Capture  */
  const val LSCR = "lscr"

  /** Matrox Uncompressed SD  */
  const val M101 = "m101"

  /** Electronic Arts Madcow Video (decoders: eamad)  */
  const val MAD = "mad"

  /** MagicYUV video  */
  const val MAGICYUV = "magicyuv"

  /** Sony PlayStation MDEC (Motion DECoder)  */
  const val MDEC = "mdec"

  /** Media 100i  */
  const val MEDIA100 = "media100"

  /** Mimic  */
  const val MIMIC = "mimic"

  /** Motion JPEG (decoders: mjpeg mjpeg_cuvid mjpeg_qsv) (encoders: mjpeg mjpeg_qsv)  */
  const val MJPEG = "mjpeg"

  /** Apple MJPEG-B  */
  const val MJPEGB = "mjpegb"

  /** American Laser Games MM Video  */
  const val MMVIDEO = "mmvideo"

  /** MobiClip Video  */
  const val MOBICLIP = "mobiclip"

  /** Motion Pixels video  */
  const val MOTIONPIXELS = "motionpixels"

  /** MPEG-1 video (decoders: mpeg1video mpeg1_cuvid)  */
  const val MPEG1VIDEO = "mpeg1video"

  /**
   * MPEG-2 video (decoders: mpeg2video mpegvideo mpeg2_qsv mpeg2_cuvid) (encoders: mpeg2video
   * mpeg2_qsv)
   */
  const val MPEG2VIDEO = "mpeg2video"

  /** MPEG-4 part 2 (decoders: mpeg4 mpeg4_cuvid) (encoders: mpeg4 libxvid)  */
  const val MPEG4 = "mpeg4"

  /** MS ATC Screen  */
  const val MSA1 = "msa1"

  /** Mandsoft Screen Capture Codec  */
  const val MSCC = "mscc"

  /** MPEG-4 part 2 Microsoft variant version 1  */
  const val MSMPEG4V1 = "msmpeg4v1"

  /** MPEG-4 part 2 Microsoft variant version 2  */
  const val MSMPEG4V2 = "msmpeg4v2"

  /** MPEG-4 part 2 Microsoft variant version 3 (decoders: msmpeg4) (encoders: msmpeg4)  */
  const val MSMPEG4V3 = "msmpeg4v3"

  /** Microsoft Paint (MSP) version 2  */
  const val MSP2 = "msp2"

  /** Microsoft RLE  */
  const val MSRLE = "msrle"

  /** MS Screen 1  */
  const val MSS1 = "mss1"

  /** MS Windows Media Video V9 Screen  */
  const val MSS2 = "mss2"

  /** Microsoft Video 1  */
  const val MSVIDEO1 = "msvideo1"

  /** LCL (LossLess Codec Library) MSZH  */
  const val MSZH = "mszh"

  /** MS Expression Encoder Screen  */
  const val MTS2 = "mts2"

  /** MidiVid 3.0  */
  const val MV30 = "mv30"

  /** Silicon Graphics Motion Video Compressor 1  */
  const val MVC1 = "mvc1"

  /** Silicon Graphics Motion Video Compressor 2  */
  const val MVC2 = "mvc2"

  /** MidiVid VQ  */
  const val MVDV = "mvdv"

  /** MidiVid Archive Codec  */
  const val MVHA = "mvha"

  /** MatchWare Screen Capture Codec  */
  const val MWSC = "mwsc"

  /** Mobotix MxPEG video  */
  const val MXPEG = "mxpeg"

  /** NotchLC  */
  const val NOTCHLC = "notchlc"

  /** NuppelVideo/RTJPEG  */
  const val NUV = "nuv"

  /** Amazing Studio Packed Animation File Video  */
  const val PAF_VIDEO = "paf_video"

  /** PAM (Portable AnyMap) image  */
  const val PAM = "pam"

  /** PBM (Portable BitMap) image  */
  const val PBM = "pbm"

  /** PC Paintbrush PCX image  */
  const val PCX = "pcx"

  /** PDV (PlayDate Video)  */
  const val PDV = "pdv"

  /** PFM (Portable FloatMap) image  */
  const val PFM = "pfm"

  /** PGM (Portable GrayMap) image  */
  const val PGM = "pgm"

  /** PGMYUV (Portable GrayMap YUV) image  */
  const val PGMYUV = "pgmyuv"

  /** PGX (JPEG2000 Test Format)  */
  const val PGX = "pgx"

  /** PHM (Portable HalfFloatMap) image  */
  const val PHM = "phm"

  /** Kodak Photo CD  */
  const val PHOTOCD = "photocd"

  /** Pictor/PC Paint  */
  const val PICTOR = "pictor"

  /** Apple Pixlet  */
  const val PIXLET = "pixlet"

  /** PNG (Portable Network Graphics) image  */
  const val PNG = "png"

  /** PPM (Portable PixelMap) image  */
  const val PPM = "ppm"

  /** Apple ProRes (iCodec Pro) (encoders: prores prores_aw prores_ks)  */
  const val PRORES = "prores"

  /** Brooktree ProSumer Video  */
  const val PROSUMER = "prosumer"

  /** Photoshop PSD file  */
  const val PSD = "psd"

  /** V.Flash PTX image  */
  const val PTX = "ptx"

  /** Apple QuickDraw  */
  const val QDRAW = "qdraw"

  /** QOI (Quite OK Image)  */
  const val QOI = "qoi"

  /** Q-team QPEG  */
  const val QPEG = "qpeg"

  /** QuickTime Animation (RLE) video  */
  const val QTRLE = "qtrle"

  /** AJA Kona 10-bit RGB Codec  */
  const val R10K = "r10k"

  /** Uncompressed RGB 10-bit  */
  const val R210 = "r210"

  /** RemotelyAnywhere Screen Capture  */
  const val RASC = "rasc"

  /** raw video  */
  const val RAWVIDEO = "rawvideo"

  /** RL2 video  */
  const val RL2 = "rl2"

  /** id RoQ video (decoders: roqvideo) (encoders: roqvideo)  */
  const val ROQ = "roq"

  /** QuickTime video (RPZA)  */
  const val RPZA = "rpza"

  /** innoHeim/Rsupport Screen Capture Codec  */
  const val RSCC = "rscc"

  /** RTV1 (RivaTuner Video)  */
  const val RTV1 = "rtv1"

  /** RealVideo 1.0  */
  const val RV10 = "rv10"

  /** RealVideo 2.0  */
  const val RV20 = "rv20"

  /** RealVideo 3.0  */
  const val RV30 = "rv30"

  /** RealVideo 4.0  */
  const val RV40 = "rv40"

  /** LucasArts SANM/SMUSH video  */
  const val SANM = "sanm"

  /** ScreenPressor  */
  const val SCPR = "scpr"

  /** Screenpresso  */
  const val SCREENPRESSO = "screenpresso"

  /** Digital Pictures SGA Video  */
  const val SGA = "sga"

  /** SGI image  */
  const val SGI = "sgi"

  /** SGI RLE 8-bit  */
  const val SGIRLE = "sgirle"

  /** BitJazz SheerVideo  */
  const val SHEERVIDEO = "sheervideo"

  /** Simbiosis Interactive IMX Video  */
  const val SIMBIOSIS_IMX = "simbiosis_imx"

  /** Smacker video (decoders: smackvid)  */
  const val SMACKVIDEO = "smackvideo"

  /** QuickTime Graphics (SMC)  */
  const val SMC = "smc"

  /** Sigmatel Motion Video  */
  const val SMVJPEG = "smvjpeg"

  /** Snow  */
  const val SNOW = "snow"

  /** Sunplus JPEG (SP5X)  */
  const val SP5X = "sp5x"

  /** NewTek SpeedHQ  */
  const val SPEEDHQ = "speedhq"

  /** Screen Recorder Gold Codec  */
  const val SRGC = "srgc"

  /** Sun Rasterfile image  */
  const val SUNRAST = "sunrast"

  /** Scalable Vector Graphics  */
  const val SVG = "svg"

  /** Sorenson Vector Quantizer 1 / Sorenson Video 1 / SVQ1  */
  const val SVQ1 = "svq1"

  /** Sorenson Vector Quantizer 3 / Sorenson Video 3 / SVQ3  */
  const val SVQ3 = "svq3"

  /** Truevision Targa image  */
  const val TARGA = "targa"

  /** Pinnacle TARGA CineWave YUV16  */
  const val TARGA_Y216 = "targa_y216"

  /** TDSC  */
  const val TDSC = "tdsc"

  /** Electronic Arts TGQ video (decoders: eatgq)  */
  const val TGQ = "tgq"

  /** Electronic Arts TGV video (decoders: eatgv)  */
  const val TGV = "tgv"

  /** Theora (encoders: libtheora)  */
  const val THEORA = "theora"

  /** Nintendo Gamecube THP video  */
  const val THP = "thp"

  /** Tiertex Limited SEQ video  */
  const val TIERTEXSEQVIDEO = "tiertexseqvideo"

  /** TIFF image  */
  const val TIFF = "tiff"

  /** 8088flex TMV  */
  const val TMV = "tmv"

  /** Electronic Arts TQI video (decoders: eatqi)  */
  const val TQI = "tqi"

  /** Duck TrueMotion 1.0  */
  const val TRUEMOTION1 = "truemotion1"

  /** Duck TrueMotion 2.0  */
  const val TRUEMOTION2 = "truemotion2"

  /** Duck TrueMotion 2.0 Real Time  */
  const val TRUEMOTION2RT = "truemotion2rt"

  /** TechSmith Screen Capture Codec (decoders: camtasia)  */
  const val TSCC = "tscc"

  /** TechSmith Screen Codec 2  */
  const val TSCC2 = "tscc2"

  /** Renderware TXD (TeXture Dictionary) image  */
  const val TXD = "txd"

  /** IBM UltiMotion (decoders: ultimotion)  */
  const val ULTI = "ulti"

  /** Ut Video  */
  const val UTVIDEO = "utvideo"

  /** Uncompressed 4:2:2 10-bit  */
  const val V210 = "v210"

  /** Uncompressed 4:2:2 10-bit  */
  const val V210X = "v210x"

  /** Uncompressed packed 4:4:4  */
  const val V308 = "v308"

  /** Uncompressed packed QT 4:4:4:4  */
  const val V408 = "v408"

  /** Uncompressed 4:4:4 10-bit  */
  const val V410 = "v410"

  /** Beam Software VB  */
  const val VB = "vb"

  /** VBLE Lossless Codec  */
  const val VBLE = "vble"

  /** Vizrt Binary Image  */
  const val VBN = "vbn"

  /** SMPTE VC-1 (decoders: vc1 vc1_qsv vc1_cuvid)  */
  const val VC1 = "vc1"

  /** Windows Media Video 9 Image v2  */
  const val VC1IMAGE = "vc1image"

  /** ATI VCR1  */
  const val VCR1 = "vcr1"

  /** Miro VideoXL (decoders: xl)  */
  const val VIXL = "vixl"

  /** Sierra VMD video  */
  const val VMDVIDEO = "vmdvideo"

  /** vMix Video  */
  const val VMIX = "vmix"

  /** VMware Screen Codec / VMware Video  */
  const val VMNC = "vmnc"

  /** Null video codec  */
  const val VNULL = "vnull"

  /** On2 VP3  */
  const val VP3 = "vp3"

  /** On2 VP4  */
  const val VP4 = "vp4"

  /** On2 VP5  */
  const val VP5 = "vp5"

  /** On2 VP6  */
  const val VP6 = "vp6"

  /** On2 VP6 (Flash version, with alpha channel)  */
  const val VP6A = "vp6a"

  /** On2 VP6 (Flash version)  */
  const val VP6F = "vp6f"

  /** On2 VP7  */
  const val VP7 = "vp7"

  /** On2 VP8 (decoders: vp8 libvpx vp8_cuvid vp8_qsv) (encoders: libvpx)  */
  const val VP8 = "vp8"

  /** Google VP9 (decoders: vp9 libvpx-vp9 vp9_cuvid vp9_qsv) (encoders: libvpx-vp9 vp9_qsv)  */
  const val VP9 = "vp9"

  /** ViewQuest VQC  */
  const val VQC = "vqc"

  /** H.266 / VVC (Versatile Video Coding)  */
  const val VVC = "vvc"

  /** WBMP (Wireless Application Protocol Bitmap) image  */
  const val WBMP = "wbmp"

  /** WinCAM Motion Video  */
  const val WCMV = "wcmv"

  /** WebP (encoders: libwebp_anim libwebp)  */
  const val WEBP = "webp"

  /** Windows Media Video 7  */
  const val WMV1 = "wmv1"

  /** Windows Media Video 8  */
  const val WMV2 = "wmv2"

  /** Windows Media Video 9  */
  const val WMV3 = "wmv3"

  /** Windows Media Video 9 Image  */
  const val WMV3IMAGE = "wmv3image"

  /** Winnov WNV1  */
  const val WNV1 = "wnv1"

  /** AVFrame to AVPacket passthrough  */
  const val WRAPPED_AVFRAME = "wrapped_avframe"

  /** Westwood Studios VQA (Vector Quantized Animation) video (decoders: vqavideo)  */
  const val WS_VQA = "ws_vqa"

  /** Wing Commander III / Xan  */
  const val XAN_WC3 = "xan_wc3"

  /** Wing Commander IV / Xxan  */
  const val XAN_WC4 = "xan_wc4"

  /** eXtended BINary text  */
  const val XBIN = "xbin"

  /** XBM (X BitMap) image  */
  const val XBM = "xbm"

  /** X-face image  */
  const val XFACE = "xface"

  /** XPM (X PixMap) image  */
  const val XPM = "xpm"

  /** XWD (X Window Dump) image  */
  const val XWD = "xwd"

  /** Uncompressed YUV 4:1:1 12-bit  */
  const val Y41P = "y41p"

  /** YUY2 Lossless Codec  */
  const val YLC = "ylc"

  /** Psygnosis YOP Video  */
  const val YOP = "yop"

  /** Uncompressed packed 4:2:0  */
  const val YUV4 = "yuv4"

  /** ZeroCodec Lossless Video  */
  const val ZEROCODEC = "zerocodec"

  /** LCL (LossLess Codec Library) ZLIB  */
  const val ZLIB = "zlib"

  /** Zip Motion Blocks Video  */
  const val ZMBV = "zmbv"
}
