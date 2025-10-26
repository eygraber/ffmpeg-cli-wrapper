package net.bramp.ffmpeg.kotlin.builder

/**
 * The available codecs may vary depending on the version of FFmpeg. <br>
 * you can get a list of available codecs through use {@link net.bramp.ffmpeg.kotlin.FFmpeg#codecs()}.
 *
 * @see net.bramp.ffmpeg.kotlin.FFmpeg#codecs()
 * @author van1164
 */
object AudioCodec {

  /** 4GV (Fourth Generation Vocoder) */
  const val GV = "4gv"

  /** 8SVX exponential */
  const val SVX_EXP = "8svx_exp"

  /** 8SVX fibonacci */
  const val SVX_FIB = "8svx_fib"

  /** AAC (Advanced Audio Coding) (decoders: aac aac_fixed) (encoders: aac aac_mf) */
  const val AAC = "aac"

  /** AAC LATM (Advanced Audio Coding LATM syntax) */
  const val AAC_LATM = "aac_latm"

  /** ATSC A/52A (AC-3) (decoders: ac3 ac3_fixed) (encoders: ac3 ac3_fixed ac3_mf) */
  const val AC3 = "ac3"

  /** AC-4 */
  const val AC4 = "ac4"

  /** Sipro ACELP.KELVIN */
  const val ACELP_KELVIN = "acelp.kelvin"

  /** ADPCM 4X Movie */
  const val ADPCM_4XM = "adpcm_4xm"

  /** SEGA CRI ADX ADPCM */
  const val ADPCM_ADX = "adpcm_adx"

  /** ADPCM Nintendo Gamecube AFC */
  const val ADPCM_AFC = "adpcm_afc"

  /** ADPCM AmuseGraphics Movie AGM */
  const val ADPCM_AGM = "adpcm_agm"

  /** ADPCM Yamaha AICA */
  const val ADPCM_AICA = "adpcm_aica"

  /** ADPCM Argonaut Games */
  const val ADPCM_ARGO = "adpcm_argo"

  /** ADPCM Creative Technology */
  const val ADPCM_CT = "adpcm_ct"

  /** ADPCM Nintendo Gamecube DTK */
  const val ADPCM_DTK = "adpcm_dtk"

  /** ADPCM Electronic Arts */
  const val ADPCM_EA = "adpcm_ea"

  /** ADPCM Electronic Arts Maxis CDROM XA */
  const val ADPCM_EA_MAXIS_XA = "adpcm_ea_maxis_xa"

  /** ADPCM Electronic Arts R1 */
  const val ADPCM_EA_R1 = "adpcm_ea_r1"

  /** ADPCM Electronic Arts R2 */
  const val ADPCM_EA_R2 = "adpcm_ea_r2"

  /** ADPCM Electronic Arts R3 */
  const val ADPCM_EA_R3 = "adpcm_ea_r3"

  /** ADPCM Electronic Arts XAS */
  const val ADPCM_EA_XAS = "adpcm_ea_xas"

  /** G.722 ADPCM (decoders: g722) (encoders: g722) */
  const val ADPCM_G722 = "adpcm_g722"

  /** G.726 ADPCM (decoders: g726) (encoders: g726) */
  const val ADPCM_G726 = "adpcm_g726"

  /** G.726 ADPCM little-endian (decoders: g726le) (encoders: g726le) */
  const val ADPCM_G726LE = "adpcm_g726le"

  /** ADPCM IMA Acorn Replay */
  const val ADPCM_IMA_ACORN = "adpcm_ima_acorn"

  /** ADPCM IMA High Voltage Software ALP */
  const val ADPCM_IMA_ALP = "adpcm_ima_alp"

  /** ADPCM IMA AMV */
  const val ADPCM_IMA_AMV = "adpcm_ima_amv"

  /** ADPCM IMA CRYO APC */
  const val ADPCM_IMA_APC = "adpcm_ima_apc"

  /** ADPCM IMA Ubisoft APM */
  const val ADPCM_IMA_APM = "adpcm_ima_apm"

  /** ADPCM IMA Cunning Developments */
  const val ADPCM_IMA_CUNNING = "adpcm_ima_cunning"

  /** ADPCM IMA Eurocom DAT4 */
  const val ADPCM_IMA_DAT4 = "adpcm_ima_dat4"

  /** ADPCM IMA Duck DK3 */
  const val ADPCM_IMA_DK3 = "adpcm_ima_dk3"

  /** ADPCM IMA Duck DK4 */
  const val ADPCM_IMA_DK4 = "adpcm_ima_dk4"

  /** ADPCM IMA Electronic Arts EACS */
  const val ADPCM_IMA_EA_EACS = "adpcm_ima_ea_eacs"

  /** ADPCM IMA Electronic Arts SEAD */
  const val ADPCM_IMA_EA_SEAD = "adpcm_ima_ea_sead"

  /** ADPCM IMA Funcom ISS */
  const val ADPCM_IMA_ISS = "adpcm_ima_iss"

  /** ADPCM IMA MobiClip MOFLEX */
  const val ADPCM_IMA_MOFLEX = "adpcm_ima_moflex"

  /** ADPCM IMA Capcom's MT Framework */
  const val ADPCM_IMA_MTF = "adpcm_ima_mtf"

  /** ADPCM IMA Dialogic OKI */
  const val ADPCM_IMA_OKI = "adpcm_ima_oki"

  /** ADPCM IMA QuickTime */
  const val ADPCM_IMA_QT = "adpcm_ima_qt"

  /** ADPCM IMA Radical */
  const val ADPCM_IMA_RAD = "adpcm_ima_rad"

  /** ADPCM IMA Loki SDL MJPEG */
  const val ADPCM_IMA_SMJPEG = "adpcm_ima_smjpeg"

  /** ADPCM IMA Simon &amp; Schuster Interactive */
  const val ADPCM_IMA_SSI = "adpcm_ima_ssi"

  /** ADPCM IMA WAV */
  const val ADPCM_IMA_WAV = "adpcm_ima_wav"

  /** ADPCM IMA Westwood */
  const val ADPCM_IMA_WS = "adpcm_ima_ws"

  /** ADPCM Microsoft */
  const val ADPCM_MS = "adpcm_ms"

  /** ADPCM MTAF */
  const val ADPCM_MTAF = "adpcm_mtaf"

  /** ADPCM Playstation */
  const val ADPCM_PSX = "adpcm_psx"

  /** ADPCM Sound Blaster Pro 2-bit */
  const val ADPCM_SBPRO_2 = "adpcm_sbpro_2"

  /** ADPCM Sound Blaster Pro 2.6-bit */
  const val ADPCM_SBPRO_3 = "adpcm_sbpro_3"

  /** ADPCM Sound Blaster Pro 4-bit */
  const val ADPCM_SBPRO_4 = "adpcm_sbpro_4"

  /** ADPCM Shockwave Flash */
  const val ADPCM_SWF = "adpcm_swf"

  /** ADPCM Nintendo THP */
  const val ADPCM_THP = "adpcm_thp"

  /** ADPCM Nintendo THP (Little-Endian) */
  const val ADPCM_THP_LE = "adpcm_thp_le"

  /** LucasArts VIMA audio */
  const val ADPCM_VIMA = "adpcm_vima"

  /** ADPCM CDROM XA */
  const val ADPCM_XA = "adpcm_xa"

  /** ADPCM Konami XMD */
  const val ADPCM_XMD = "adpcm_xmd"

  /** ADPCM Yamaha */
  const val ADPCM_YAMAHA = "adpcm_yamaha"

  /** ADPCM Zork */
  const val ADPCM_ZORK = "adpcm_zork"

  /** ALAC (Apple Lossless Audio Codec) */
  const val ALAC = "alac"

  /**
   * AMR-NB (Adaptive Multi-Rate NarrowBand) (decoders: amrnb libopencore_amrnb) (encoders:
   * libopencore_amrnb)
   */
  const val AMR_NB = "amr_nb"

  /**
   * AMR-WB (Adaptive Multi-Rate WideBand) (decoders: amrwb libopencore_amrwb) (encoders:
   * libvo_amrwbenc)
   */
  const val AMR_WB = "amr_wb"

  /** Null audio codec */
  const val ANULL = "anull"

  /** Marian's A-pac audio */
  const val APAC = "apac"

  /** Monkey's Audio */
  const val APE = "ape"

  /** aptX (Audio Processing Technology for Bluetooth) */
  const val APTX = "aptx"

  /** aptX HD (Audio Processing Technology for Bluetooth) */
  const val APTX_HD = "aptx_hd"

  /** ATRAC1 (Adaptive TRansform Acoustic Coding) */
  const val ATRAC1 = "atrac1"

  /** ATRAC3 (Adaptive TRansform Acoustic Coding 3) */
  const val ATRAC3 = "atrac3"

  /** ATRAC3 AL (Adaptive TRansform Acoustic Coding 3 Advanced Lossless) */
  const val ATRAC3AL = "atrac3al"

  /** ATRAC3+ (Adaptive TRansform Acoustic Coding 3+) (decoders: atrac3plus) */
  const val ATRAC3P = "atrac3p"

  /**
   * ATRAC3+ AL (Adaptive TRansform Acoustic Coding 3+ Advanced Lossless) (decoders: atrac3plusal)
   */
  const val ATRAC3PAL = "atrac3pal"

  /** ATRAC9 (Adaptive TRansform Acoustic Coding 9) */
  const val ATRAC9 = "atrac9"

  /** On2 Audio for Video Codec (decoders: on2avc) */
  const val AVC = "avc"

  /** Bink Audio (DCT) */
  const val BINKAUDIO_DCT = "binkaudio_dct"

  /** Bink Audio (RDFT) */
  const val BINKAUDIO_RDFT = "binkaudio_rdft"

  /** Discworld II BMV audio */
  const val BMV_AUDIO = "bmv_audio"

  /** Bonk audio */
  const val BONK = "bonk"

  /** DPCM Cuberoot-Delta-Exact */
  const val CBD2_DPCM = "cbd2_dpcm"

  /** Constrained Energy Lapped Transform (CELT) */
  const val CELT = "celt"

  /** codec2 (very low bitrate speech codec) */
  const val CODEC2 = "codec2"

  /** RFC 3389 Comfort Noise */
  const val COMFORTNOISE = "comfortnoise"

  /** Cook / Cooker / Gecko (RealAudio G2) */
  const val COOK = "cook"

  /** DPCM Xilam DERF */
  const val DERF_DPCM = "derf_dpcm"

  /** DFPWM (Dynamic Filter Pulse Width Modulation) */
  const val DFPWM = "dfpwm"

  /** Dolby E */
  const val DOLBY_E = "dolby_e"

  /** DSD (Direct Stream Digital), least significant bit first */
  const val DSD_LSBF = "dsd_lsbf"

  /** DSD (Direct Stream Digital), least significant bit first, planar */
  const val DSD_LSBF_PLANAR = "dsd_lsbf_planar"

  /** DSD (Direct Stream Digital), most significant bit first */
  const val DSD_MSBF = "dsd_msbf"

  /** DSD (Direct Stream Digital), most significant bit first, planar */
  const val DSD_MSBF_PLANAR = "dsd_msbf_planar"

  /** Delphine Software International CIN audio */
  const val DSICINAUDIO = "dsicinaudio"

  /** Digital Speech Standard - Standard Play mode (DSS SP) */
  const val DSS_SP = "dss_sp"

  /** DST (Direct Stream Transfer) */
  const val DST = "dst"

  /** DCA (DTS Coherent Acoustics) (decoders: dca) (encoders: dca) */
  const val DTS = "dts"

  /** DV audio */
  const val DVAUDIO = "dvaudio"

  /** ATSC A/52B (AC-3, E-AC-3) */
  const val EAC3 = "eac3"

  /** EVRC (Enhanced Variable Rate Codec) */
  const val EVRC = "evrc"

  /** MobiClip FastAudio */
  const val FASTAUDIO = "fastaudio"

  /** FLAC (Free Lossless Audio Codec) */
  const val FLAC = "flac"

  /** FTR Voice */
  const val FTR = "ftr"

  /** G.723.1 */
  const val G723_1 = "g723_1"

  /** G.729 */
  const val G729 = "g729"

  /** DPCM Gremlin */
  const val GREMLIN_DPCM = "gremlin_dpcm"

  /** GSM (decoders: gsm libgsm) (encoders: libgsm) */
  const val GSM = "gsm"

  /** GSM Microsoft variant (decoders: gsm_ms libgsm_ms) (encoders: libgsm_ms) */
  const val GSM_MS = "gsm_ms"

  /** CRI HCA */
  const val HCA = "hca"

  /** HCOM Audio */
  const val HCOM = "hcom"

  /** IAC (Indeo Audio Coder) */
  const val IAC = "iac"

  /** iLBC (Internet Low Bitrate Codec) */
  const val ILBC = "ilbc"

  /** IMC (Intel Music Coder) */
  const val IMC = "imc"

  /** DPCM Interplay */
  const val INTERPLAY_DPCM = "interplay_dpcm"

  /** Interplay ACM */
  const val INTERPLAYACM = "interplayacm"

  /** MACE (Macintosh Audio Compression/Expansion) 3:1 */
  const val MACE3 = "mace3"

  /** MACE (Macintosh Audio Compression/Expansion) 6:1 */
  const val MACE6 = "mace6"

  /** Voxware MetaSound */
  const val METASOUND = "metasound"

  /** Micronas SC-4 Audio */
  const val MISC4 = "misc4"

  /** MLP (Meridian Lossless Packing) */
  const val MLP = "mlp"

  /** MP1 (MPEG audio layer 1) (decoders: mp1 mp1float) */
  const val MP1 = "mp1"

  /** MP2 (MPEG audio layer 2) (decoders: mp2 mp2float) (encoders: mp2 mp2fixed) */
  const val MP2 = "mp2"

  /** MP3 (MPEG audio layer 3) (decoders: mp3float mp3) (encoders: libmp3lame mp3_mf) */
  const val MP3 = "mp3"

  /** ADU (Application Data Unit) MP3 (MPEG audio layer 3) (decoders: mp3adufloat mp3adu) */
  const val MP3ADU = "mp3adu"

  /** MP3onMP4 (decoders: mp3on4float mp3on4) */
  const val MP3ON4 = "mp3on4"

  /** MPEG-4 Audio Lossless Coding (ALS) (decoders: als) */
  const val MP4ALS = "mp4als"

  /** MPEG-H 3D Audio */
  const val MPEGH_3D_AUDIO = "mpegh_3d_audio"

  /** MSN Siren */
  const val MSNSIREN = "msnsiren"

  /** Musepack SV7 (decoders: mpc7) */
  const val MUSEPACK7 = "musepack7"

  /** Musepack SV8 (decoders: mpc8) */
  const val MUSEPACK8 = "musepack8"

  /** Nellymoser Asao */
  const val NELLYMOSER = "nellymoser"

  /** Opus (Opus Interactive Audio Codec) (decoders: opus libopus) (encoders: opus libopus) */
  const val OPUS = "opus"

  /** OSQ (Original Sound Quality) */
  const val OSQ = "osq"

  /** Amazing Studio Packed Animation File Audio */
  const val PAF_AUDIO = "paf_audio"

  /** PCM A-law / G.711 A-law */
  const val PCM_ALAW = "pcm_alaw"

  /** PCM signed 16|20|24-bit big-endian for Blu-ray media */
  const val PCM_BLURAY = "pcm_bluray"

  /** PCM signed 20|24-bit big-endian */
  const val PCM_DVD = "pcm_dvd"

  /** PCM 16.8 floating point little-endian */
  const val PCM_F16LE = "pcm_f16le"

  /** PCM 24.0 floating point little-endian */
  const val PCM_F24LE = "pcm_f24le"

  /** PCM 32-bit floating point big-endian */
  const val PCM_F32BE = "pcm_f32be"

  /** PCM 32-bit floating point little-endian */
  const val PCM_F32LE = "pcm_f32le"

  /** PCM 64-bit floating point big-endian */
  const val PCM_F64BE = "pcm_f64be"

  /** PCM 64-bit floating point little-endian */
  const val PCM_F64LE = "pcm_f64le"

  /** PCM signed 20-bit little-endian planar */
  const val PCM_LXF = "pcm_lxf"

  /** PCM mu-law / G.711 mu-law */
  const val PCM_MULAW = "pcm_mulaw"

  /** PCM signed 16-bit big-endian */
  const val PCM_S16BE = "pcm_s16be"

  /** PCM signed 16-bit big-endian planar */
  const val PCM_S16BE_PLANAR = "pcm_s16be_planar"

  /** PCM signed 16-bit little-endian */
  const val PCM_S16LE = "pcm_s16le"

  /** PCM signed 16-bit little-endian planar */
  const val PCM_S16LE_PLANAR = "pcm_s16le_planar"

  /** PCM signed 24-bit big-endian */
  const val PCM_S24BE = "pcm_s24be"

  /** PCM D-Cinema audio signed 24-bit */
  const val PCM_S24DAUD = "pcm_s24daud"

  /** PCM signed 24-bit little-endian */
  const val PCM_S24LE = "pcm_s24le"

  /** PCM signed 24-bit little-endian planar */
  const val PCM_S24LE_PLANAR = "pcm_s24le_planar"

  /** PCM signed 32-bit big-endian */
  const val PCM_S32BE = "pcm_s32be"

  /** PCM signed 32-bit little-endian */
  const val PCM_S32LE = "pcm_s32le"

  /** PCM signed 32-bit little-endian planar */
  const val PCM_S32LE_PLANAR = "pcm_s32le_planar"

  /** PCM signed 64-bit big-endian */
  const val PCM_S64BE = "pcm_s64be"

  /** PCM signed 64-bit little-endian */
  const val PCM_S64LE = "pcm_s64le"

  /** PCM signed 8-bit */
  const val PCM_S8 = "pcm_s8"

  /** PCM signed 8-bit planar */
  const val PCM_S8_PLANAR = "pcm_s8_planar"

  /** PCM SGA */
  const val PCM_SGA = "pcm_sga"

  /** PCM unsigned 16-bit big-endian */
  const val PCM_U16BE = "pcm_u16be"

  /** PCM unsigned 16-bit little-endian */
  const val PCM_U16LE = "pcm_u16le"

  /** PCM unsigned 24-bit big-endian */
  const val PCM_U24BE = "pcm_u24be"

  /** PCM unsigned 24-bit little-endian */
  const val PCM_U24LE = "pcm_u24le"

  /** PCM unsigned 32-bit big-endian */
  const val PCM_U32BE = "pcm_u32be"

  /** PCM unsigned 32-bit little-endian */
  const val PCM_U32LE = "pcm_u32le"

  /** PCM unsigned 8-bit */
  const val PCM_U8 = "pcm_u8"

  /** PCM Archimedes VIDC */
  const val PCM_VIDC = "pcm_vidc"

  /** QCELP / PureVoice */
  const val QCELP = "qcelp"

  /** QDesign Music Codec 2 */
  const val QDM2 = "qdm2"

  /** QDesign Music */
  const val QDMC = "qdmc"

  /** RealAudio 1.0 (14.4K) (decoders: real_144) (encoders: real_144) */
  const val RA_144 = "ra_144"

  /** RealAudio 2.0 (28.8K) (decoders: real_288) */
  const val RA_288 = "ra_288"

  /** RealAudio Lossless */
  const val RALF = "ralf"

  /** RKA (RK Audio) */
  const val RKA = "rka"

  /** DPCM id RoQ */
  const val ROQ_DPCM = "roq_dpcm"

  /** SMPTE 302M */
  const val S302M = "s302m"

  /** SBC (low-complexity subband codec) */
  const val SBC = "sbc"

  /** DPCM Squareroot-Delta-Exact */
  const val SDX2_DPCM = "sdx2_dpcm"

  /** Shorten */
  const val SHORTEN = "shorten"

  /** RealAudio SIPR / ACELP.NET */
  const val SIPR = "sipr"

  /** Siren */
  const val SIREN = "siren"

  /** Smacker audio (decoders: smackaud) */
  const val SMACKAUDIO = "smackaudio"

  /** SMV (Selectable Mode Vocoder) */
  const val SMV = "smv"

  /** DPCM Sol */
  const val SOL_DPCM = "sol_dpcm"

  /** Sonic */
  const val SONIC = "sonic"

  /** Sonic lossless */
  const val SONICLS = "sonicls"

  /** Speex (decoders: speex libspeex) (encoders: libspeex) */
  const val SPEEX = "speex"

  /** TAK (Tom's lossless Audio Kompressor) */
  const val TAK = "tak"

  /** TrueHD */
  const val TRUEHD = "truehd"

  /** DSP Group TrueSpeech */
  const val TRUESPEECH = "truespeech"

  /** TTA (True Audio) */
  const val TTA = "tta"

  /** VQF TwinVQ */
  const val TWINVQ = "twinvq"

  /** Sierra VMD audio */
  const val VMDAUDIO = "vmdaudio"

  /** Vorbis (decoders: vorbis libvorbis) (encoders: vorbis libvorbis) */
  const val VORBIS = "vorbis"

  /** DPCM Marble WADY */
  const val WADY_DPCM = "wady_dpcm"

  /** Waveform Archiver */
  const val WAVARC = "wavarc"

  /** Wave synthesis pseudo-codec */
  const val WAVESYNTH = "wavesynth"

  /** WavPack */
  const val WAVPACK = "wavpack"

  /** Westwood Audio (SND1) (decoders: ws_snd1) */
  const val WESTWOOD_SND1 = "westwood_snd1"

  /** Windows Media Audio Lossless */
  const val WMALOSSLESS = "wmalossless"

  /** Windows Media Audio 9 Professional */
  const val WMAPRO = "wmapro"

  /** Windows Media Audio 1 */
  const val WMAV1 = "wmav1"

  /** Windows Media Audio 2 */
  const val WMAV2 = "wmav2"

  /** Windows Media Audio Voice */
  const val WMAVOICE = "wmavoice"

  /** DPCM Xan */
  const val XAN_DPCM = "xan_dpcm"

  /** Xbox Media Audio 1 */
  const val XMA1 = "xma1"

  /** Xbox Media Audio 2 */
  const val XMA2 = "xma2"
}
