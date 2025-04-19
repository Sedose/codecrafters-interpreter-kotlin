package io.codecrafters

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream

fun Application.runAndCaptureOutput(arguments: Array<String>): String =
  ByteArrayOutputStream().use { outputStream ->
    withSystemOutRedirectedTo(outputStream) {
      this.run(arguments)
    }
    outputStream.toString().trim()
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
