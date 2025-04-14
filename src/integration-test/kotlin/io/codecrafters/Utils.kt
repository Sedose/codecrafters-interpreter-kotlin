package io.codecrafters

import java.io.OutputStream
import java.io.PrintStream

inline fun <T> withSystemOutRedirectedTo(
  stream: OutputStream,
  block: () -> T,
): T {
  val originalOut = System.out
  System.setOut(PrintStream(stream))
  return try {
    block()
  } finally {
    System.setOut(originalOut)
  }
}
