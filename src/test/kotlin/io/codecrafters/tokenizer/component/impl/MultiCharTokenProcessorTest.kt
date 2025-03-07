package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.impl.model.CanProcessCase
import io.codecrafters.tokenizer.component.impl.model.ProcessCase
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
class MultiCharTokenProcessorTest {
  private val processor = MultiCharTokenProcessor()

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
        Named.of("Valid pair at start", CanProcessCase("==", 0, true)),
      ),
      Arguments.of(
        Named.of("Valid pair in middle", CanProcessCase("a!=b", 1, true)),
      ),
      Arguments.of(
        Named.of("Next index out of bounds", CanProcessCase("=", 0, false)),
      ),
      Arguments.of(
        Named.of("Invalid pair", CanProcessCase("++", 0, false)),
      ),
      Arguments.of(
        Named.of("Negative index", CanProcessCase("==", -1, false)),
      ),
      Arguments.of(
        Named.of("Empty string", CanProcessCase("", 0, false)),
      ),
      Arguments.of(
        Named.of("Valid pair at end", CanProcessCase(">=", 0, true)),
      ),
    )

  @ParameterizedTest
  @MethodSource("processDataProvider")
  fun testProcess(testCase: ProcessCase) {
    val result = processor.process(testCase.input, testCase.startIndex, 1)
    assertEquals(testCase.expectedType, result.token?.type)
    assertEquals(testCase.expectedLexeme, result.token?.lexeme)
    assertEquals(testCase.expectedNewIndex, result.newIndex)
    assertNull(result.error)
  }

  @Suppress("UnusedPrivateMember")
  private fun processDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "EQUAL_EQUAL",
          ProcessCase("==", 0, TokenType.EQUAL_EQUAL, "==", 2),
        ),
      ),
      Arguments.of(
        Named.of(
          "BANG_EQUAL in middle",
          ProcessCase("a!=b", 1, TokenType.BANG_EQUAL, "!=", 3),
        ),
      ),
      Arguments.of(
        Named.of(
          "LESS_EQUAL",
          ProcessCase("<=", 0, TokenType.LESS_EQUAL, "<=", 2),
        ),
      ),
      Arguments.of(
        Named.of(
          "GREATER_EQUAL at end",
          ProcessCase("x>=5", 1, TokenType.GREATER_EQUAL, ">=", 3),
        ),
      ),
    )
}
