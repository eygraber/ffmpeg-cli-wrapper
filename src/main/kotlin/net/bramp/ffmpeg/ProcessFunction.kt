package net.bramp.ffmpeg

import java.io.IOException

/**
 * Runs a process returning a Reader to its stdout
 *
 * @author bramp
 */
fun interface ProcessFunction {
  @Throws(IOException::class)
  fun run(args: List<String>): Process
}
