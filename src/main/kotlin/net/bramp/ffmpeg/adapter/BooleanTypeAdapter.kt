package net.bramp.ffmpeg.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class BooleanTypeAdapter : JsonDeserializer<Boolean> {
  @Suppress("ReturnCount")
  @Throws(JsonParseException::class)
  override fun deserialize(
    json: JsonElement?,
    typeOfT: Type?,
    context: JsonDeserializationContext?,
  ): Boolean? {
    if(json == null || json.isJsonNull) {
      return null
    }

    if(json.isJsonPrimitive) {
      val primitive = json.asJsonPrimitive
      if(primitive.isBoolean) {
        return primitive.asBoolean
      }
      if(primitive.isString) {
        val stringValue = primitive.asString
        if(stringValue.equals("true", ignoreCase = true)) return true
        if(stringValue.equals("false", ignoreCase = true)) return false
        // For any other string (e.g. "1", "0", "yes"), original Java code returned null from this block.
        return null
      }
      // This path is taken if json is a number (e.g. 1, 0), not a string like "1" or "0".
      if(primitive.isNumber) {
        return primitive.asInt != 0
      }
    }

    // This part is reached if json is not a boolean primitive, not a string primitive, and not a number primitive.
    // Or if it's a JSON literal that is a number but somehow not caught by primitive.isNumber (e.g. "10" as a string was already handled).
    // The original Java code's final `return json.getAsInt() != 0;` would apply.
    // This typically means it's a JSON number literal. For other types (Object/Array), getAsInt() would throw.
    try {
      // This will handle JSON numbers like 1, 0.
      // If json is an Object or Array, asInt will throw an exception.
      return json.asInt != 0
    }
    catch(e: UnsupportedOperationException) {
      throw JsonParseException(
        "Cannot parse JSON element to Boolean: Not a primitive or direct number. Element: $json",
        e,
      )
    }
    catch(e: IllegalStateException) {
      throw JsonParseException(
        "Cannot parse JSON element to Boolean: Not a primitive or direct number. Element: $json",
        e,
      )
    }
    catch(e: NumberFormatException) {
      // This could happen if json element is a string that wasn't "true"/"false" (returned null above),
      // and also isn't a valid integer string. However, string primitives are fully handled above.
      // So this is more for safety or unexpected GSON internal behavior if asInt is called on something strange.
      throw JsonParseException("Cannot parse JSON element to Boolean: Not a valid number string. Element: $json", e)
    }
  }
}
