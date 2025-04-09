package io.codecrafters.tokenizer

import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.impl.IdentifierProcessor
import io.codecrafters.tokenizer.component.impl.MultiCharTokenProcessor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * Here I want integration-style testing to ensure the Tokenizer and processors behave properly together.
 */
@TestInstance(Lifecycle.PER_CLASS)
class TokenizerTest {
  private val tokenizer =
    Tokenizer(
      listOf(
        MultiCharTokenProcessor(),
        IdentifierProcessor(),
      ),
    )

  @ParameterizedTest
  @MethodSource("tokenizeDataProvider")
  fun testTokenize(testCase: TokenizeCase) {
    val result = tokenizer.tokenize(testCase.input)
    assertEquals(testCase.expectedTokens.size, result.tokens.size)
    for (i in testCase.expectedTokens.indices) {
      val (expectedType, expectedLexeme) = testCase.expectedTokens[i]
      val actual = result.tokens[i]
      assertEquals(expectedType, actual.type)
      assertEquals(expectedLexeme, actual.lexeme)
    }
    assertEquals(testCase.expectedErrors.size, result.errors.size)
    for (i in testCase.expectedErrors.indices) {
      assertEquals(testCase.expectedErrors[i], result.errors[i])
    }
  }

  @Suppress("UnusedPrivateMember", "LongMethod")
  private fun tokenizeDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "Empty input",
          TokenizeCase(
            input = "",
            expectedTokens = emptyList(),
            expectedErrors = emptyList(),
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Single identifier",
          TokenizeCase(
            input = "foo",
            expectedTokens = listOf(TokenData(TokenType.IDENTIFIER, "foo")),
            expectedErrors = emptyList(),
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Reserved word",
          TokenizeCase(
            input = "fun",
            expectedTokens = listOf(TokenData(TokenType.FUN, "fun")),
            expectedErrors = emptyList(),
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Multi-char token",
          TokenizeCase(
            input = "==",
            expectedTokens = listOf(TokenData(TokenType.EQUAL_EQUAL, "==")),
            expectedErrors = emptyList(),
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Unknown character",
          TokenizeCase(
            input = "?",
            expectedTokens = emptyList(),
            expectedErrors = listOf("[line 1] Error: Unexpected character: ?"),
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Mixed input with newline error",
          TokenizeCase(
            input = "foo\n? bar",
            expectedTokens =
              listOf(
                TokenData(TokenType.IDENTIFIER, "foo"),
                TokenData(TokenType.IDENTIFIER, "bar"),
              ),
            expectedErrors = listOf("[line 2] Error: Unexpected character: ?"),
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Mixed tokens",
          TokenizeCase(
            input = "fun foo==bar",
            expectedTokens =
              listOf(
                TokenData(TokenType.FUN, "fun"),
                TokenData(TokenType.IDENTIFIER, "foo"),
                TokenData(TokenType.EQUAL_EQUAL, "=="),
                TokenData(TokenType.IDENTIFIER, "bar"),
              ),
            expectedErrors = emptyList(),
          ),
        ),
      ),
    )
}

data class TokenizeCase(
  val input: String,
  val expectedTokens: List<TokenData>,
  val expectedErrors: List<String>,
)

data class TokenData(
  val type: TokenType,
  val lexeme: String,
)
