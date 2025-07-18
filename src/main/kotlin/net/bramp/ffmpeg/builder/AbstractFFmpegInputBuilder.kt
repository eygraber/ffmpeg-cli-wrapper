package net.bramp.ffmpeg.builder

import com.google.common.collect.ImmutableList
import net.bramp.ffmpeg.options.EncodingOptions
import net.bramp.ffmpeg.probe.FFmpegProbeResult

abstract class AbstractFFmpegInputBuilder<T : AbstractFFmpegInputBuilder<T>> :
  AbstractFFmpegStreamBuilder<T> {

  val probeResult: FFmpegProbeResult?

  private var isReadAtNativeFrameRate: Boolean = false

  /**
   * Number of times input stream shall be looped. Loop 0 means no loop, loop -1 means infinite
   * loop.
   */
  private var streamLoop: Int = 0

  protected constructor(parent: FFmpegBuilder, filename: String) : this(parent, null, filename)

  protected constructor(
    parent: FFmpegBuilder,
    probeResult: FFmpegProbeResult?,
    filename: String,
  ) : super(parent, filename) {
    this.probeResult = probeResult
  }

  fun readAtNativeFrameRate(): T {
    this.isReadAtNativeFrameRate = true
    return getThis()
  }

  /**
   * Sets number of times input stream shall be looped. Loop 0 means no loop, loop -1 means infinite
   * loop.
   *
   * @param streamLoop loop counter
   * @return this
   */
  fun setStreamLoop(streamLoop: Int): T {
    this.streamLoop = streamLoop
    return getThis()
  }

  // getThis() is assumed to be inherited from AbstractFFmpegStreamBuilder.kt

  override fun buildOptions(): EncodingOptions? = null

  override fun addGlobalFlags(parent: FFmpegBuilder, args: ImmutableList.Builder<String>) {
    if(this.isReadAtNativeFrameRate) {
      args.add("-re")
    }

    if(this.streamLoop != 0) {
      args.add("-stream_loop", this.streamLoop.toString())
    }

    super.addGlobalFlags(parent, args)
  }

  fun getStreamLoop(): Int = streamLoop
}
