package net.bramp.ffmpeg.info

import java.io.BufferedReader
import java.io.IOException

object InfoParser {
  @Throws(IOException::class)
  fun parseLayouts(r: BufferedReader): List<ChannelLayout> {
    val individualChannelLookup = mutableMapOf<String, IndividualChannel>()
    val channelLayouts = mutableListOf<ChannelLayout>()
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
        val (name, description) = line.split(" ", limit = 2)
        val individualChannel = IndividualChannel(name, description.trim())
        channelLayouts.add(individualChannel)
        individualChannelLookup[individualChannel.name] = individualChannel
      }
      else if(isParsingChannelLayouts) {
        val (name, composition) = line.split(" ", limit = 2)
        val decomposition = buildList {
          for(channelName in composition.trim().split('+')) {
            add(
              requireNotNull(individualChannelLookup[channelName]) {
                "No channel found for $channelName"
              },
            )
          }
        }
        channelLayouts.add(StandardChannelLayout(name, decomposition))
      }
    }

    return channelLayouts
  }
}
