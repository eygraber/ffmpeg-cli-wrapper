package net.bramp.ffmpeg.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import net.bramp.ffmpeg.probe.FFmpegFrame
import net.bramp.ffmpeg.probe.FFmpegFrameOrPacket
import net.bramp.ffmpeg.probe.FFmpegPacket
import java.lang.reflect.Type

class FFmpegPacketsAndFramesAdapter : JsonDeserializer<FFmpegFrameOrPacket> {
  @Throws(JsonParseException::class)
  override fun deserialize(
    jsonElement: JsonElement?,
    type: Type?,
    jsonDeserializationContext: JsonDeserializationContext?,
  ): FFmpegFrameOrPacket? {
    if(jsonElement == null || jsonElement.isJsonNull) {
      return null
    }

    if(jsonElement is JsonObject) {
      requireNotNull(jsonDeserializationContext) { "JsonDeserializationContext cannot be null" }

      val typeElement = jsonElement.get("type")
        ?: throw JsonParseException("Missing 'type' field in JsonObject for FFmpegFrameOrPacket: $jsonElement")

      if(!typeElement.isJsonPrimitive || !typeElement.asJsonPrimitive.isString) {
        throw JsonParseException("'type' field must be a string in JsonObject for FFmpegFrameOrPacket: $jsonElement")
      }

      val objectType = typeElement.asString

      return if(objectType == "packet") {
        jsonDeserializationContext.deserialize(jsonElement, FFmpegPacket::class.java)
      } else {
        // Assuming "frame" or any other type defaults to FFmpegFrame
        jsonDeserializationContext.deserialize(jsonElement, FFmpegFrame::class.java)
      }
    }

    // Original code returned null if not a JsonObject.
    // Depending on strictness, could throw JsonParseException here as well.
    // e.g., throw JsonParseException("Expected JsonObject for FFmpegFrameOrPacket but got: ${jsonElement::class.simpleName}")
    return null
  }
}
