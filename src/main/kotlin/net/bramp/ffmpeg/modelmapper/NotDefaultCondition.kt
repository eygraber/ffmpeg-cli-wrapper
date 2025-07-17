package net.bramp.ffmpeg.modelmapper

import com.google.common.base.Defaults
import com.google.common.base.Objects
import org.modelmapper.Condition
import org.modelmapper.spi.MappingContext

/**
 * Only maps properties which are not their type's default value.
 *
 * @param <S> source type
 * @param <D> destination type
 * @author bramp
</D></S> */
class NotDefaultCondition<S, D> : Condition<S, D> {
  override fun applies(
    context: MappingContext<S, D>,
  ): Boolean = !Objects.equal(context.source, Defaults.defaultValue(context.sourceType))

  companion object {
    val notDefault: NotDefaultCondition<Any, Any> = NotDefaultCondition()
  }
}
