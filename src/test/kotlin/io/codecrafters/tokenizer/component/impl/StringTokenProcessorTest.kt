package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.impl.model.CanProcessCase
import io.codecrafters.tokenizer.component.impl.model.ErrorProcessCase
import io.codecrafters.tokenizer.component.impl.model.SuccessProcessCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(Lifecycle.PER_CLASS)
class StringTokenProcessorTest {
  private val processor = StringTokenProcessor()

  @ParameterizedTest
  @MethodSource("canProcessDataProvider")
  fun testCanProcess(testCase: CanProcessCase) {
    val result = processor.canProcess(testCase.input, testCase.startIndex)
    assertEquals(testCase.expectCanProcess, result)
  }

  @Suppress("UnusedPrivateMember")
  private fun canProcessDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "Starts with quote",
          CanProcessCase("\"hello\"", 0, true),
        ),
      ),
      Arguments.of(
        Named.of(
          "No quote at start",
          CanProcessCase("hello", 0, false),
        ),
      ),
      Arguments.of(
        Named.of(
          "Quote but out of range (end of string)",
          CanProcessCase("\"hello\"", 7, false),
        ),
      ),
      Arguments.of(
        Named.of(
          "Empty string",
          CanProcessCase("", 0, false),
        ),
      ),
    )

  @ParameterizedTest
  @MethodSource("processSuccessDataProvider")
  fun testProcess_Success(testCase: SuccessProcessCase) {
    val (token, newIndex, error) = processor.process(testCase.input, testCase.startIndex, 1)
    assertNotNull(token)
    assertEquals(TokenType.STRING, token.type)
    assertEquals(testCase.expectedLexeme, token.lexeme)
    assertEquals(testCase.expectedNewIndex, newIndex)
    // The literal should be the substring without the surrounding quotes
    val expectedLiteral = testCase.expectedLexeme.substring(1, testCase.expectedLexeme.lastIndex)
    assertEquals(expectedLiteral, token.literal)
    assertNull(error)
  }

  @Suppress("UnusedPrivateMember")
  private fun processSuccessDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "Basic string",
          SuccessProcessCase(
            input = "\"hello\"",
            startIndex = 0,
            expectedNewIndex = 7,
            expectedType = TokenType.STRING, // not strictly used here, but included for reference
            expectedLexeme = "\"hello\"",
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "String with punctuation",
          SuccessProcessCase(
            input = "\"!@#$%^&*()\"",
            startIndex = 0,
            expectedNewIndex = 12,
            expectedType = TokenType.STRING,
            expectedLexeme = "\"!@#$%^&*()\"",
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Multiple quotes but stops at first closing quote",
          SuccessProcessCase(
            input = "\"foo\"\"bar\"",
            startIndex = 0,
            expectedNewIndex = 5,
            expectedType = TokenType.STRING,
            expectedLexeme = "\"foo\"",
          ),
        ),
      ),
    )

  @ParameterizedTest
  @MethodSource("processErrorDataProvider")
  fun testProcess_Error(testCase: ErrorProcessCase) {
    val (token, newIndex, error) = processor.process(testCase.input, testCase.startIndex, 1)
    assertNull(token)
    assertEquals(testCase.expectedNewIndex, newIndex)
    assertEquals(testCase.expectedError, error)
  }

  @Suppress("UnusedPrivateMember")
  private fun processErrorDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "Unterminated string (no closing quote)",
          ErrorProcessCase(
            input = "\"hello world",
            startIndex = 0,
            expectedNewIndex = 12,
            expectedError = "[line 1] Error: Unterminated string.",
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Unterminated before newline",
          ErrorProcessCase(
            input = "\"line1\nline2\"",
            startIndex = 0,
            expectedNewIndex = 6,
            expectedError = "[line 1] Error: Unterminated string.",
          ),
        ),
      ),
    )
}
