package net.bramp.ffmpeg.lang

import java.io.InputStream
import java.io.OutputStream

/**
 * A Mock Process, which exits with zero, and returns the provided streams.
 *
 * @author bramp
 */
class MockProcess : Process {
  private val stdin: OutputStream?
  private val stdout: InputStream
  private val stderr: InputStream?

  constructor(stdout: InputStream) {
    this.stdin = null // TODO make this something
    this.stdout = stdout
    this.stderr = null // TODO make this return nothing.
  }

  constructor(stdin: OutputStream?, stdout: InputStream, stderr: InputStream?) {
    this.stdin = stdin
    this.stdout = stdout
    this.stderr = stderr
  }

  override fun getOutputStream(): OutputStream? = stdin

  override fun getInputStream(): InputStream = stdout

  override fun getErrorStream(): InputStream? = stderr

  override fun waitFor(): Int = 0

  override fun exitValue(): Int = 0

  override fun destroy() {}
}
