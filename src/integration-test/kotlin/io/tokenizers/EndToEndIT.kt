package io.tokenizers

import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

data class TokenizerTestCase(
  val resourcePath: String,
  val expectedLines: List<String>,
)

class EndToEndIT {
  companion object {
    @JvmStatic
    fun testCases() =
      listOf(
        TokenizerTestCase(
          "src/integration-test/resources/parentheses.lex",
          listOf(
            "LEFT_PAREN ( null",
            "LEFT_PAREN ( null",
            "RIGHT_PAREN ) null",
            "EOF  null",
          ),
        ),
        TokenizerTestCase(
          "src/integration-test/resources/braces_test.lox",
          listOf(
            "LEFT_BRACE { null",
            "LEFT_BRACE { null",
            "RIGHT_BRACE } null",
            "RIGHT_BRACE } null",
            "EOF  null",
          ),
        ),
      )
  }

  @ParameterizedTest
  @MethodSource("testCases")
  fun `execute jar with command and file`(testCase: TokenizerTestCase) {
    val (resourcePath, expectedLines) = testCase
    val process =
      ProcessBuilder(
        "java",
        "-jar",
        "target/build-your-own-interpreter.jar",
        "tokenize",
        resourcePath,
      ).redirectErrorStream(true).start()
    val exitCode = process.waitFor()
    assertTrue(exitCode == 0)
    assertLinesMatch(
      expectedLines,
      process.inputStream
        .bufferedReader()
        .readText()
        .lines()
        .dropLastWhile { it.isBlank() },
    )
  }
}
