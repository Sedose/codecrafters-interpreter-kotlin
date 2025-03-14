package io.tokenizers

import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EndToEndIT {
  @Test
  fun `execute jar with command and file`() {
    val process =
      ProcessBuilder(
        "java",
        "-jar",
        "target/build-your-own-interpreter.jar",
        "tokenize",
        "src/test/resources/sample.lex",
      ).redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readText()
    val exitCode = process.waitFor()

    assertTrue(exitCode == 0, "Process exited with non-zero exit code: $exitCode")
    assertLinesMatch(
      listOf(
        "LEFT_PAREN ( null",
        "LEFT_PAREN ( null",
        "RIGHT_PAREN ) null",
        "EOF  null",
      ),
      output.lines().dropLastWhile { it.isBlank() },
    )
  }
}
