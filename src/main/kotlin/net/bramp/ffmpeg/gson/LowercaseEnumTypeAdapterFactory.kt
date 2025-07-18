package net.bramp.ffmpeg.gson

import com.google.common.collect.ImmutableMap
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.Locale
import javax.annotation.CheckReturnValue

/**
 * Maps Enums to lowercase strings.
 *
 *
 * Adapted from: [TypeAdapterFactory](https://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/TypeAdapterFactory.html)
 */
class LowercaseEnumTypeAdapterFactory : TypeAdapterFactory {

  private class MyTypeAdapter<T> internal constructor(lowercaseToEnum: Map<String, T>) :
    TypeAdapter<T>() {

    // T is a Enum, thus immutable, however, we can't enforce that type due to the
    // TypeAdapterFactory interface
    private val lowercaseToEnum: ImmutableMap<String, T> = ImmutableMap.copyOf(lowercaseToEnum)

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: T?) {
      if(value == null) {
        out.nullValue()
      }
      else {
        out.value(toLowercase(value as Any))
      }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): T? {
      if(reader.peek() == JsonToken.NULL) {
        reader.nextNull()
        return null
      }
      return lowercaseToEnum[reader.nextString()]
    }
  }

  @CheckReturnValue
  override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
    val rawType = type.rawType as? Class<T> ?: return null
    if(!rawType.isEnum) {
      return null
    }

    // Setup mapping of consts
    val lowercaseToEnum = mutableMapOf<String, T>()
    for(constant in rawType.enumConstants) {
      lowercaseToEnum[toLowercase(requireNotNull(constant) as Any)] = constant
    }

    return MyTypeAdapter(lowercaseToEnum)
  }

  companion object {
    @CheckReturnValue
    private fun toLowercase(o: Any): String = o.toString().lowercase(Locale.UK)
  }
}
