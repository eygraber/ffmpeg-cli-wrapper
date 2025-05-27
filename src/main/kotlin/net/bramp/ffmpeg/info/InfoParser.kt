package net.bramp.ffmpeg.info

import com.google.common.base.Splitter
import java.io.BufferedReader
import java.io.IOException

object InfoParser {
  @Throws(IOException::class)
  fun parseLayouts(r: BufferedReader): List<ChannelLayout> {
    val individualChannelLookup: MutableMap<String, IndividualChannel> = HashMap()
    val channelLayouts: MutableList<ChannelLayout> = ArrayList()
    var isParsingIndividualChannels = false
    var isParsingChannelLayouts = false

    r.forEachLine { line ->
      if(line.startsWith("NAME") || line.isEmpty()) {
        // Skip header and empty lines
      }
      else if(line == "Individual channels:") {
        isParsingIndividualChannels = true
        isParsingChannelLayouts = false
      }
      else if(line == "Standard channel layouts:") {
        isParsingIndividualChannels = false
        isParsingChannelLayouts = true
      }
      else if(isParsingIndividualChannels) {
        val s = line.split(" ".toRegex(), 2).toTypedArray()
        val individualChannel = IndividualChannel(s[0], s[1].trim())
        channelLayouts.add(individualChannel)
        individualChannelLookup[individualChannel.name] = individualChannel
      }
      else if(isParsingChannelLayouts) {
        val s = line.split(" ".toRegex(), 2).toTypedArray()
        val decomposition: MutableList<IndividualChannel> = ArrayList()
        for(channelName in Splitter.on('+').split(s[1].trim())) {
          decomposition.add(
            requireNotNull(individualChannelLookup[channelName]) {
              "No channel found for $channelName"
            },
          )
        }
        channelLayouts.add(
          StandardChannelLayout(s[0], decomposition.toList()),
        )
      }
    }

    return channelLayouts
  }
}
