package net.bramp.ffmpeg.info

import com.google.common.base.Splitter
import java.io.BufferedReader
import java.io.IOException

object InfoParser {
  @Throws(IOException::class)
  fun parseLayouts(r: BufferedReader): List<ChannelLayout> {
    val individualChannelLookup: MutableMap<String, IndividualChannel> = HashMap()
    val channelLayouts: MutableList<ChannelLayout> = ArrayList()
    var line: String?
    var parsingIndividualChannels = false
    var parsingChannelLayouts = false
    while(r.readLine().also { line = it } != null) {
      if (line!!.startsWith("NAME") || line!!.isEmpty()) {
        // Skip header and empty lines
        continue
      }
      else if (line == "Individual channels:") {
        parsingIndividualChannels = true
        parsingChannelLayouts = false
      }
      else if (line == "Standard channel layouts:") {
        parsingIndividualChannels = false
        parsingChannelLayouts = true
      }
      else if (parsingIndividualChannels) {
        val s = line!!.split(" ".toRegex(), 2).toTypedArray()
        val individualChannel = IndividualChannel(s[0], s[1].trim())
        channelLayouts.add(individualChannel)
        individualChannelLookup[individualChannel.name] = individualChannel
      }
      else if (parsingChannelLayouts) {
        val s = line!!.split(" ".toRegex(), 2).toTypedArray()
        val decomposition: MutableList<IndividualChannel> = ArrayList()
        for (channelName in Splitter.on('+').split(s[1].trim())) {
          decomposition.add(individualChannelLookup[channelName]!!)
        }
        channelLayouts.add(
          StandardChannelLayout(s[0], decomposition.toList()),
        )
      }
    }
    return channelLayouts
  }
}
