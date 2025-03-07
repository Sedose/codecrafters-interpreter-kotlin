package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.RESERVED_WORDS
import io.codecrafters.model.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(Lifecycle.PER_CLASS)
class IdentifierProcessorParameterizedTest {
  private val processor = IdentifierProcessor()

  @ParameterizedTest
  @MethodSource("canProcessDataProvider")
  fun testCanProcess(testCase: CanProcessCase) {
    val result =
      processor.canProcess(
        input = testCase.input,
        index = testCase.startIndex,
      )
    assertEquals(testCase.expectCanProcess, result)
  }

  @Suppress("UnusedPrivateMember")
  private fun canProcessDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of("Valid identifier char at start", CanProcessCase("abc", 0, true)),
      ),
      Arguments.of(
        Named.of("Valid identifier char in middle", CanProcessCase("a_bc", 1, true)),
      ),
      Arguments.of(
        Named.of("Invalid identifier char", CanProcessCase(" a", 0, false)),
      ),
      Arguments.of(
        Named.of("Index at end of string (out of range)", CanProcessCase("abc", 3, false)),
      ),
      Arguments.of(
        Named.of("Negative index", CanProcessCase("abc", -1, false)),
      ),
      Arguments.of(
        Named.of("Empty string", CanProcessCase("", 0, false)),
      ),
    )

  @ParameterizedTest
  @MethodSource("processDataProvider")
  fun testProcess(testCase: ProcessCase) {
    val (token, newIndex, error) =
      processor.process(
        input = testCase.input,
        index = testCase.startIndex,
        lineNumber = 1,
      )
    assertEquals(testCase.expectedType, token?.type)
    assertEquals(testCase.expectedLexeme, token?.lexeme)
    assertEquals(testCase.expectedNewIndex, newIndex)
    assertNull(error)
  }

  @Suppress("UnusedPrivateMember", "LongMethod")
  private fun processDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "Simple identifier",
          ProcessCase(
            input = "foo",
            startIndex = 0,
            expectedType = TokenType.IDENTIFIER,
            expectedLexeme = "foo",
            expectedNewIndex = 3,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Reserved word",
          ProcessCase(
            input = "fun",
            startIndex = 0,
            expectedType = RESERVED_WORDS["fun"] ?: TokenType.IDENTIFIER,
            expectedLexeme = "fun",
            expectedNewIndex = 3,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Alphanumeric",
          ProcessCase(
            input = "foo123",
            startIndex = 0,
            expectedType = TokenType.IDENTIFIER,
            expectedLexeme = "foo123",
            expectedNewIndex = 6,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Underscore",
          ProcessCase(
            input = "_bar",
            startIndex = 0,
            expectedType = TokenType.IDENTIFIER,
            expectedLexeme = "_bar",
            expectedNewIndex = 4,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Stops on special char",
          ProcessCase(
            input = "abc!",
            startIndex = 0,
            expectedType = TokenType.IDENTIFIER,
            expectedLexeme = "abc",
            expectedNewIndex = 3,
          ),
        ),
      ),
    )
}

data class CanProcessCase(
  val input: String,
  val startIndex: Int,
  val expectCanProcess: Boolean,
)

data class ProcessCase(
  val input: String,
  val startIndex: Int,
  val expectedType: TokenType,
  val expectedLexeme: String,
  val expectedNewIndex: Int,
)
