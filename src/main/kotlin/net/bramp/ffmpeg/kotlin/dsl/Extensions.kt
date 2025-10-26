package net.bramp.ffmpeg.kotlin.dsl

import net.bramp.ffmpeg.kotlin.FFmpegExecutor
import net.bramp.ffmpeg.kotlin.FFprobe
import net.bramp.ffmpeg.kotlin.job.FFmpegJob
import net.bramp.ffmpeg.kotlin.progress.ProgressListener

/**
 * Execute an FFmpeg job using a Kotlin DSL.
 *
 * Example:
 * ```
 * executor.run {
 *   input("input.mp4")
 *   output("output.mp4") {
 *     videoCodec = "libx264"
 *   }
 * }
 * ```
 */
fun FFmpegExecutor.run(block: FFmpegDsl.() -> Unit) {
  val builder = ffmpeg(block)
  createJob(builder).run()
}

/**
 * Execute an FFmpeg two-pass job using a Kotlin DSL.
 *
 * Example:
 * ```
 * executor.runTwoPass {
 *   input("input.mp4")
 *   output("output.mp4") {
 *     videoCodec = "libx264"
 *     videoBitRate = 1_000_000
 *   }
 * }
 * ```
 */
fun FFmpegExecutor.runTwoPass(block: FFmpegDsl.() -> Unit) {
  val builder = ffmpeg(block)
  createTwoPassJob(builder).run()
}

/**
 * Create an FFmpeg job using a Kotlin DSL.
 *
 * Example:
 * ```
 * val job = executor.job {
 *   input("input.mp4")
 *   output("output.mp4") {
 *     videoCodec = "libx264"
 *   }
 * }
 * job.run()
 * ```
 */
fun FFmpegExecutor.job(block: FFmpegDsl.() -> Unit): FFmpegJob {
  val builder = ffmpeg(block)
  return createJob(builder)
}

/**
 * Create an FFmpeg job with progress listener using a Kotlin DSL.
 *
 * Example:
 * ```
 * val job = executor.job(progressListener) {
 *   input("input.mp4")
 *   output("output.mp4") {
 *     videoCodec = "libx264"
 *   }
 * }
 * job.run()
 * ```
 */
fun FFmpegExecutor.job(
  progressListener: ProgressListener,
  block: FFmpegDsl.() -> Unit,
): FFmpegJob {
  val builder = ffmpeg(block)
  return createJob(builder, progressListener)
}

/**
 * Create a two-pass FFmpeg job using a Kotlin DSL.
 *
 * Example:
 * ```
 * val job = executor.twoPassJob {
 *   input("input.mp4")
 *   output("output.mp4") {
 *     videoCodec = "libx264"
 *     videoBitRate = 1_000_000
 *   }
 * }
 * job.run()
 * ```
 */
fun FFmpegExecutor.twoPassJob(block: FFmpegDsl.() -> Unit): FFmpegJob {
  val builder = ffmpeg(block)
  return createTwoPassJob(builder)
}

/**
 * Create a two-pass FFmpeg job with progress listener using a Kotlin DSL.
 *
 * Example:
 * ```
 * val job = executor.twoPassJob(progressListener) {
 *   input("input.mp4")
 *   output("output.mp4") {
 *     videoCodec = "libx264"
 *     videoBitRate = 1_000_000
 *   }
 * }
 * job.run()
 * ```
 */
fun FFmpegExecutor.twoPassJob(
  progressListener: ProgressListener,
  block: FFmpegDsl.() -> Unit,
): FFmpegJob {
  val builder = ffmpeg(block)
  return createTwoPassJob(builder, progressListener)
}

/**
 * Probe a file using FFprobe with a Kotlin DSL.
 *
 * Example:
 * ```
 * val result = ffprobe.probe {
 *   input = "input.mp4"
 *   showFormat = true
 *   showStreams = true
 * }
 * ```
 */
fun FFprobe.probe(block: FFprobeDsl.() -> Unit) = probe(ffprobe(block).build())

/**
 * Build FFmpeg command arguments using a Kotlin DSL.
 *
 * Example:
 * ```
 * val args = buildFFmpegCommand {
 *   input("input.mp4")
 *   output("output.mp4") {
 *     videoCodec = "libx264"
 *   }
 * }
 * ```
 */
fun buildFFmpegCommand(block: FFmpegDsl.() -> Unit): List<String> = ffmpeg(block).build()

/**
 * Build FFprobe command arguments using a Kotlin DSL.
 *
 * Example:
 * ```
 * val args = buildFFprobeCommand {
 *   input = "input.mp4"
 *   showFormat = true
 * }
 * ```
 */
fun buildFFprobeCommand(block: FFprobeDsl.() -> Unit): List<String> = ffprobe(block).build()
