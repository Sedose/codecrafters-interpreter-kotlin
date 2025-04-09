package io.codecrafters.tokenizer.component.impl

import io.codecrafters.tokenizer.component.impl.model.CanProcessCase
import io.codecrafters.tokenizer.component.impl.model.SuccessProcessCase
import io.codecrafters.tokenizer.model.TokenType
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
class SingleCharTokenProcessorTest {
  private val processor = SingleCharTokenProcessor()

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
        Named.of("Valid single char at start", CanProcessCase("+", 0, true)),
      ),
      Arguments.of(
        Named.of("Valid char in middle", CanProcessCase("a+b", 1, true)),
      ),
      Arguments.of(
        Named.of("Invalid character", CanProcessCase("a", 0, false)),
      ),
      Arguments.of(
        Named.of("Index out of bounds", CanProcessCase("(", 1, false)),
      ),
      Arguments.of(
        Named.of("Negative index", CanProcessCase(")", -1, false)),
      ),
      Arguments.of(
        Named.of("Empty string", CanProcessCase("", 0, false)),
      ),
    )

  @ParameterizedTest
  @MethodSource("processDataProvider")
  fun testProcess(testCase: SuccessProcessCase) {
    val result = processor.process(testCase.input, testCase.startIndex, 1)
    assertEquals(testCase.expectedType, result.token?.type)
    assertEquals(testCase.expectedLexeme, result.token?.lexeme)
    assertEquals(testCase.expectedNewIndex, result.newIndex)
    assertNull(result.error)
  }

  @Suppress("UnusedPrivateMember", "LongMethod")
  private fun processDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "Plus operator",
          SuccessProcessCase(
            input = "+",
            startIndex = 0,
            expectedType = TokenType.PLUS,
            expectedLexeme = "+",
            expectedNewIndex = 1,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Left parenthesis",
          SuccessProcessCase(
            input = "(x)",
            startIndex = 0,
            expectedType = TokenType.LEFT_PAREN,
            expectedLexeme = "(",
            expectedNewIndex = 1,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Equal sign",
          SuccessProcessCase(
            input = "=5",
            startIndex = 0,
            expectedType = TokenType.EQUAL,
            expectedLexeme = "=",
            expectedNewIndex = 1,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Semicolon in middle",
          SuccessProcessCase(
            input = "x;y",
            startIndex = 1,
            expectedType = TokenType.SEMICOLON,
            expectedLexeme = ";",
            expectedNewIndex = 2,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Right brace",
          SuccessProcessCase(
            input = "}",
            startIndex = 0,
            expectedType = TokenType.RIGHT_BRACE,
            expectedLexeme = "}",
            expectedNewIndex = 1,
          ),
        ),
      ),
    )
}
