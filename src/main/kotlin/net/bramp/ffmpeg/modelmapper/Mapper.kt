package net.bramp.ffmpeg.modelmapper

import net.bramp.ffmpeg.builder.AbstractFFmpegStreamBuilder
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder
import net.bramp.ffmpeg.modelmapper.NotDefaultCondition.Companion.notDefault
import net.bramp.ffmpeg.options.AudioEncodingOptions
import net.bramp.ffmpeg.options.EncodingOptions
import net.bramp.ffmpeg.options.MainEncodingOptions
import net.bramp.ffmpeg.options.VideoEncodingOptions
import org.modelmapper.ModelMapper
import org.modelmapper.TypeMap
import org.modelmapper.config.Configuration
import org.modelmapper.convention.NameTokenizers

/**
 * Copies values from one type of object to another
 *
 * @author bramp
 */
object Mapper {
  private val modelMapper = newModelMapper()

  private fun <S, D> createTypeMap(
    mapper: ModelMapper,
    sourceType: Class<S>,
    destinationType: Class<D>,
    config: Configuration,
  ): TypeMap<S, D> = mapper
    // We setPropertyCondition because ModelMapper seems to ignore this in the config
    .createTypeMap(sourceType, destinationType, config)
    .setPropertyCondition(config.propertyCondition)

  private fun newModelMapper(): ModelMapper {
    val modelMapper = ModelMapper()
    val config = modelMapper
      .configuration
      .copy()
      .setFieldMatchingEnabled(true)
      .setPropertyCondition(notDefault)
      .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
    createTypeMap(
      mapper = modelMapper,
      sourceType = MainEncodingOptions::class.java,
      destinationType = FFmpegOutputBuilder::class.java,
      config = config,
    )
    createTypeMap(
      mapper = modelMapper,
      sourceType = AudioWrapper::class.java,
      destinationType = FFmpegOutputBuilder::class.java,
      config = config,
    )
    createTypeMap(
      mapper = modelMapper,
      sourceType = VideoWrapper::class.java,
      destinationType = FFmpegOutputBuilder::class.java,
      config = config,
    )
    return modelMapper
  }

  /** Simple wrapper object, to inject the word "audio" in the property name  */
  internal class AudioWrapper(val audio: AudioEncodingOptions)

  /** Simple wrapper object, to inject the word "video" in the property name  */
  internal class VideoWrapper(val video: VideoEncodingOptions)

  fun <T : AbstractFFmpegStreamBuilder<*>> map(opts: MainEncodingOptions, dest: T) {
    modelMapper.map(opts, dest)
  }

  fun <T : AbstractFFmpegStreamBuilder<*>> map(opts: AudioEncodingOptions, dest: T) {
    modelMapper.map(AudioWrapper(opts), dest)
  }

  fun <T : AbstractFFmpegStreamBuilder<*>> map(opts: VideoEncodingOptions, dest: T) {
    modelMapper.map(VideoWrapper(opts), dest)
  }

  fun <T : AbstractFFmpegStreamBuilder<*>> map(opts: EncodingOptions, dest: T) {
    map(opts.main, dest)
    if(opts.audio.isEnabled) {
      map(opts.audio, dest)
    }
    if(opts.video.isEnabled) {
      map(opts.video, dest)
    }
  }
}
