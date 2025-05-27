package net.bramp.ffmpeg.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import net.bramp.ffmpeg.probe.FFmpegStream.SideData
import java.lang.reflect.Type

class FFmpegStreamSideDataAdapter : JsonDeserializer<SideData?> {
  @OptIn(ExperimentalStdlibApi::class)
  @Throws(JsonParseException::class)
  override fun deserialize(
    jsonElement: JsonElement?,
    type: Type?,
    jsonDeserializationContext: JsonDeserializationContext,
  ): SideData? {
    if(jsonElement !is JsonObject) return null
    try {
      return SideData(
        jsonDeserializationContext.deserialize(
          jsonElement.get("side_data_type"),
          String::class.java,
        ),
        jsonDeserializationContext.deserialize(
          jsonElement.get("displaymatrix"),
          String::class.java,
        ),
        jsonDeserializationContext.deserialize(
          jsonElement.get("rotation"),
          Int::class.java,
        ),
      )
    }
    catch(_: Exception) {
      return null
    }
  }
}
