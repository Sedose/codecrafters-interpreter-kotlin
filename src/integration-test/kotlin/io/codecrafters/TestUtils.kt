package io.codecrafters

import io.codecrafters.application.Application
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream

fun Application.runAndCaptureOutput(arguments: Array<String>): String =
  ByteArrayOutputStream().use { buffer ->
    withSystemOutRedirectedTo(buffer) { run(arguments) }
    buffer.toString().trim()
  }

inline fun <T> withSystemOutRedirectedTo(
  outputStream: OutputStream,
  block: () -> T,
): T {
  val originalOut = System.out
  System.setOut(PrintStream(outputStream))
  return try {
    block()
  } finally {
    System.setOut(originalOut)
  }
}
