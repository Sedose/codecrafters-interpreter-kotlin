package io.tokenizers

import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

data class TokenizerTestCase(
  val resourcePath: String,
  val expectedLines: List<String>,
  val expectedExitCode: Int = 0,
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
        TokenizerTestCase(
          "src/integration-test/resources/single_char_tokens.lox",
          listOf(
            "LEFT_PAREN ( null",
            "LEFT_BRACE { null",
            "STAR * null",
            "DOT . null",
            "COMMA , null",
            "PLUS + null",
            "STAR * null",
            "RIGHT_BRACE } null",
            "RIGHT_PAREN ) null",
            "EOF  null",
          ),
        ),
        TokenizerTestCase(
          "src/integration-test/resources/invalid_tokens.lox",
          listOf(
            "[line 1] Error: Unexpected character: $",
            "[line 1] Error: Unexpected character: #",
            "COMMA , null",
            "DOT . null",
            "LEFT_PAREN ( null",
            "EOF  null",
          ),
          expectedExitCode = 65,
        ),
      )
  }

  @ParameterizedTest
  @MethodSource("testCases")
  fun `execute jar with command and file`(testCase: TokenizerTestCase) {
    val (resourcePath, expectedLines, expectedExitCode) = testCase
    val process =
      ProcessBuilder(
        "java",
        "-jar",
        "target/build-your-own-interpreter.jar",
        "tokenize",
        resourcePath,
      ).redirectErrorStream(true)
        .start()

    val exitCode = process.waitFor()
    assertTrue(exitCode == expectedExitCode)
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
