package net.bramp.ffmpeg.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import net.bramp.ffmpeg.probe.FFmpegStream

class FFmpegStreamSideDataAdapter : JsonDeserializer<FFmpegStream.SideData> {
  @Throws(JsonParseException::class)
  override fun deserialize(
      jsonElement: JsonElement?,
      type: Type?,
      jsonDeserializationContext: JsonDeserializationContext?
  ): FFmpegStream.SideData? {
    if (jsonElement == null || jsonElement.isJsonNull || jsonElement !is JsonObject) {
      return null
    }
    requireNotNull(jsonDeserializationContext) { "JsonDeserializationContext cannot be null" }

    try {
      // Assuming FFmpegStream.SideData is a class with a public no-arg constructor.
      // This is how newInstance() via reflection would work.
      val sideDataInstance = FFmpegStream.SideData() 

      val sideDataClass = FFmpegStream.SideData::class.java
      for (field in sideDataClass.fields) { // .fields gets public (Java) fields
        val fieldName = field.name
        val jsonFieldValue = jsonElement.get(fieldName)

        if (jsonFieldValue != null && !jsonFieldValue.isJsonNull) {
          // Using field.type for deserialization. This is generally what Gson would need.
          // The original used field.getAnnotatedType().getType(), which is more complex
          // and often not necessary unless dealing with very specific generic annotations.
          val deserializedValue = jsonDeserializationContext.deserialize<Any?>(jsonFieldValue, field.type)
          
          // Try to set the field. This part is sensitive to Kotlin's property vs Java field differences.
          // If SideData is a Kotlin class, direct field access like this might not work for properties
          // unless they are @JvmField.
          try {
            field.isAccessible = true // Ensure field is accessible
            field.set(sideDataInstance, deserializedValue)
          } catch (e: IllegalAccessException) {
            // Log this, as it indicates a potential issue with the SideData class structure
            // or the reflection approach for that specific field.
            System.err.println(
                "FFmpegStreamSideDataAdapter: Could not set field '${field.name}' " +
                "for ${sideDataClass.simpleName} due to IllegalAccessException. " +
                "Is it a public (Java) field or a @JvmField Kotlin property? Error: ${e.message}"
            )
            // Depending on strictness, might re-throw or collect errors. Original returns null for any exception.
          }
        }
      }
      return sideDataInstance
    } catch (e: Exception) {
      // Broad catch to mimic original behavior of returning null on any error.
      // Consider logging 'e' here to understand failures if they occur.
      // e.g., LOG.warn("Failed to deserialize FFmpegStream.SideData", e)
      System.err.println("FFmpegStreamSideDataAdapter: Error deserializing SideData: ${e.message}")
      return null
    }
  }
}
