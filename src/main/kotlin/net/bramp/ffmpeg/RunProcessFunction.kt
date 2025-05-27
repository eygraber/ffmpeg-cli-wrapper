package net.bramp.ffmpeg

import com.google.common.base.Joiner
import com.google.common.base.Preconditions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

/**
 * Simple function that creates a Process with the arguments, and returns a BufferedReader reading
 * stdout
 *
 * @author bramp
 */
class RunProcessFunction : ProcessFunction {

  var workingDirectory: File? = null
    private set // To encourage use of setter methods for fluent interface

  @Throws(IOException::class)
  override fun run(args: List<String>): Process {
    // args is non-null by type List<String>. If it could be null from Java, use List<String>?
    Preconditions.checkArgument(args.isNotEmpty(), "No arguments specified")

    if(LOG.isInfoEnabled) {
      LOG.info("{}", Joiner.on(" ").join(args))
    }

    val builder = ProcessBuilder(args)
    workingDirectory?.let {
      builder.directory(it)
    }
    builder.redirectErrorStream(true)
    return builder.start()
  }

  fun setWorkingDirectory(workingDirectoryPath: String): RunProcessFunction {
    this.workingDirectory = File(workingDirectoryPath)
    return this
  }

  fun setWorkingDirectory(workingDirectory: File): RunProcessFunction {
    this.workingDirectory = workingDirectory
    return this
  }

  companion object {
    internal val LOG: Logger = LoggerFactory.getLogger(RunProcessFunction::class.java)
  }
}
