package net.bramp.ffmpeg.io

import org.slf4j.Logger
import org.slf4j.event.Level
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class LoggerOutputStream(
  private val logger: Logger,
  private val level: Level,
) : OutputStream() {
  private val buffer = ByteArrayOutputStream()

  override fun write(b: Int) {
    buffer.write(b)
    if(b == '\n'.code) {
      val line = buffer.toString(StandardCharsets.UTF_8.name())
      when(level) {
        Level.TRACE -> logger.trace(line)
        Level.DEBUG -> logger.debug(line)
        Level.INFO -> logger.info(line)
        Level.WARN -> logger.warn(line)
        Level.ERROR -> logger.error(line)
      }
      buffer.reset()
    }
  }
}
