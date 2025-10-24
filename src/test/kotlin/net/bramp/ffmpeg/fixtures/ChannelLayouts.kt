package net.bramp.ffmpeg.fixtures

import net.bramp.ffmpeg.info.ChannelLayout
import net.bramp.ffmpeg.info.IndividualChannel
import net.bramp.ffmpeg.info.StandardChannelLayout

object ChannelLayouts {
  private val FL = IndividualChannel("FL", "front left")
  private val FR = IndividualChannel("FR", "front right")
  private val FC = IndividualChannel("FC", "front center")
  private val LFE = IndividualChannel("LFE", "low frequency")
  private val BL = IndividualChannel("BL", "back left")
  private val BR = IndividualChannel("BR", "back right")
  private val FLC = IndividualChannel("FLC", "front left-of-center")
  private val FRC = IndividualChannel("FRC", "front right-of-center")
  private val BC = IndividualChannel("BC", "back center")
  private val SL = IndividualChannel("SL", "side left")
  private val SR = IndividualChannel("SR", "side right")
  private val TC = IndividualChannel("TC", "top center")
  private val TFL = IndividualChannel("TFL", "top front left")
  private val TFC = IndividualChannel("TFC", "top front center")
  private val TFR = IndividualChannel("TFR", "top front right")
  private val TBL = IndividualChannel("TBL", "top back left")
  private val TBC = IndividualChannel("TBC", "top back center")
  private val TBR = IndividualChannel("TBR", "top back right")
  private val DL = IndividualChannel("DL", "downmix left")
  private val DR = IndividualChannel("DR", "downmix right")
  private val WL = IndividualChannel("WL", "wide left")
  private val WR = IndividualChannel("WR", "wide right")
  private val SDL = IndividualChannel("SDL", "surround direct left")
  private val SDR = IndividualChannel("SDR", "surround direct right")
  private val LFE2 = IndividualChannel("LFE2", "low frequency 2")
  private val TSL = IndividualChannel("TSL", "top side left")
  private val TSR = IndividualChannel("TSR", "top side right")
  private val BFC = IndividualChannel("BFC", "bottom front center")
  private val BFL = IndividualChannel("BFL", "bottom front left")
  private val BFR = IndividualChannel("BFR", "bottom front right")

  private fun decomposition(vararg channels: IndividualChannel) = channels.toList()

  val CHANNEL_LAYOUTS: List<ChannelLayout> = listOf(
    FL,
    FR,
    FC,
    LFE,
    BL,
    BR,
    FLC,
    FRC,
    BC,
    SL,
    SR,
    TC,
    TFL,
    TFC,
    TFR,
    TBL,
    TBC,
    TBR,
    DL,
    DR,
    WL,
    WR,
    SDL,
    SDR,
    LFE2,
    TSL,
    TSR,
    BFC,
    BFL,
    BFR,
    StandardChannelLayout("mono", decomposition(FC)),
    StandardChannelLayout("stereo", decomposition(FL, FR)),
    StandardChannelLayout("2.1", decomposition(FL, FR, LFE)),
    StandardChannelLayout("3.0", decomposition(FL, FR, FC)),
    StandardChannelLayout("3.0(back)", decomposition(FL, FR, BC)),
    StandardChannelLayout("4.0", decomposition(FL, FR, FC, BC)),
    StandardChannelLayout("quad", decomposition(FL, FR, BL, BR)),
    StandardChannelLayout("quad(side)", decomposition(FL, FR, SL, SR)),
    StandardChannelLayout("3.1", decomposition(FL, FR, FC, LFE)),
    StandardChannelLayout("5.0", decomposition(FL, FR, FC, BL, BR)),
    StandardChannelLayout("5.0(side)", decomposition(FL, FR, FC, SL, SR)),
    StandardChannelLayout("4.1", decomposition(FL, FR, FC, LFE, BC)),
    StandardChannelLayout("5.1", decomposition(FL, FR, FC, LFE, BL, BR)),
    StandardChannelLayout("5.1(side)", decomposition(FL, FR, FC, LFE, SL, SR)),
    StandardChannelLayout("6.0", decomposition(FL, FR, FC, BC, SL, SR)),
    StandardChannelLayout("6.0(front)", decomposition(FL, FR, FLC, FRC, SL, SR)),
    StandardChannelLayout("hexagonal", decomposition(FL, FR, FC, BL, BR, BC)),
    StandardChannelLayout("6.1", decomposition(FL, FR, FC, LFE, BC, SL, SR)),
    StandardChannelLayout("6.1(back)", decomposition(FL, FR, FC, LFE, BL, BR, BC)),
    StandardChannelLayout("6.1(front)", decomposition(FL, FR, LFE, FLC, FRC, SL, SR)),
    StandardChannelLayout("7.0", decomposition(FL, FR, FC, BL, BR, SL, SR)),
    StandardChannelLayout("7.0(front)", decomposition(FL, FR, FC, FLC, FRC, SL, SR)),
    StandardChannelLayout("7.1", decomposition(FL, FR, FC, LFE, BL, BR, SL, SR)),
    StandardChannelLayout("7.1(wide)", decomposition(FL, FR, FC, LFE, BL, BR, FLC, FRC)),
    StandardChannelLayout("7.1(wide-side)", decomposition(FL, FR, FC, LFE, FLC, FRC, SL, SR)),
    StandardChannelLayout("7.1(top)", decomposition(FL, FR, FC, LFE, BL, BR, TFL, TFR)),
    StandardChannelLayout("octagonal", decomposition(FL, FR, FC, BL, BR, BC, SL, SR)),
    StandardChannelLayout("cube", decomposition(FL, FR, BL, BR, TFL, TFR, TBL, TBR)),
    StandardChannelLayout(
      "hexadecagonal",
      decomposition(FL, FR, FC, BL, BR, BC, SL, SR, TFL, TFC, TFR, TBL, TBC, TBR, WL, WR)
    ),
    StandardChannelLayout("downmix", decomposition(DL, DR)),
    StandardChannelLayout(
      "22.2",
      decomposition(
        FL,
        FR,
        FC,
        LFE,
        BL,
        BR,
        FLC,
        FRC,
        BC,
        SL,
        SR,
        TC,
        TFL,
        TFC,
        TFR,
        TBL,
        TBC,
        TBR,
        LFE2,
        TSL,
        TSR,
        BFC,
        BFL,
        BFR
      )
    )
  )
}
