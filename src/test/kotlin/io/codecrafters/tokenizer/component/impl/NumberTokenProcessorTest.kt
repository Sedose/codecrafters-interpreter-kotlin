package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.impl.model.CanProcessCase
import io.codecrafters.tokenizer.component.impl.model.ErrorProcessCase
import io.codecrafters.tokenizer.component.impl.model.ProcessCase
import io.codecrafters.tokenizer.component.impl.model.SuccessProcessCase
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
class NumberTokenProcessorTest {
  private val processor = NumberTokenProcessor()

  @ParameterizedTest
  @MethodSource("canProcessDataProvider")
  fun testCanProcess(testCase: CanProcessCase) {
    val result = processor.canProcess(testCase.input, testCase.startIndex)
    assertEquals(testCase.expectCanProcess, result)
  }

  @Suppress("UnusedPrivateMember")
  private fun canProcessDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(Named.of("Digit at start", CanProcessCase("123", 0, true))),
      Arguments.of(Named.of("Non-digit character", CanProcessCase("a45", 0, false))),
      Arguments.of(Named.of("Index out of bounds", CanProcessCase("5", 1, false))),
      Arguments.of(Named.of("Negative index", CanProcessCase("9", -1, false))),
      Arguments.of(Named.of("Decimal point first", CanProcessCase(".5", 0, false))),
      Arguments.of(Named.of("Digit in middle", CanProcessCase("a5b", 1, true))),
    )

  @ParameterizedTest
  @MethodSource("processDataProvider")
  fun testProcess(testCase: ProcessCase) {
    val result = processor.process(testCase.input, testCase.startIndex, 1)

    when (testCase) {
      is SuccessProcessCase -> {
        assertEquals(testCase.expectedType, result.token?.type)
        assertEquals(testCase.expectedLexeme, result.token?.lexeme)
        assertEquals(testCase.expectedValue, result.token?.literal)
        assertEquals(testCase.expectedNewIndex, result.newIndex)
        assertNull(result.error)
      }
      is ErrorProcessCase -> {
        assertNull(result.token)
        assertEquals(testCase.expectedError, result.error)
        assertEquals(testCase.expectedNewIndex, result.newIndex)
      }
    }
  }

  @Suppress("UnusedPrivateMember", "LongMethod")
  private fun processDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "Integer number",
          SuccessProcessCase(
            input = "123",
            startIndex = 0,
            expectedType = TokenType.NUMBER,
            expectedLexeme = "123",
            expectedNewIndex = 3,
            expectedValue = 123.0,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Decimal number",
          SuccessProcessCase(
            input = "123.45",
            startIndex = 0,
            expectedType = TokenType.NUMBER,
            expectedLexeme = "123.45",
            expectedNewIndex = 6,
            expectedValue = 123.45,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Multiple decimal points",
          ErrorProcessCase(
            input = "12.34.56",
            startIndex = 0,
            expectedNewIndex = 5,
            expectedError = "[line 1] Error: Unexpected character: .",
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Number with trailing letter",
          SuccessProcessCase(
            input = "456abc",
            startIndex = 0,
            expectedType = TokenType.NUMBER,
            expectedLexeme = "456",
            expectedNewIndex = 3,
            expectedValue = 456.0,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Number ending with dot",
          SuccessProcessCase(
            input = "789.",
            startIndex = 0,
            expectedType = TokenType.NUMBER,
            expectedLexeme = "789.",
            expectedNewIndex = 4,
            expectedValue = 789.0,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Single digit",
          SuccessProcessCase(
            input = "0",
            startIndex = 0,
            expectedType = TokenType.NUMBER,
            expectedLexeme = "0",
            expectedNewIndex = 1,
            expectedValue = 0.0,
          ),
        ),
      ),
    )
}
