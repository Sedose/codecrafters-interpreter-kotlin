package io.codecrafters

import io.codecrafters.model.TokenType
import io.codecrafters.parser.Expr
import io.codecrafters.parser.Parser
import io.codecrafters.tokenizer.model.Token
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ParserTest {
  companion object {
    @JvmStatic
    fun provideLiteralTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(Token(TokenType.NUMBER, "42.47", "42.47", 1), Token(TokenType.EOF, "", "", 1)),
          42.47,
        ),
        Arguments.of(
          listOf(Token(TokenType.TRUE, "true", "true", 1), Token(TokenType.EOF, "", "", 1)),
          true,
        ),
        Arguments.of(
          listOf(Token(TokenType.FALSE, "false", "false", 1), Token(TokenType.EOF, "", "", 1)),
          false,
        ),
        Arguments.of(
          listOf(Token(TokenType.NIL, "nil", "nil", 1), Token(TokenType.EOF, "", "", 1)),
          null,
        ),
        Arguments.of(
          listOf(Token(TokenType.STRING, "\"hello\"", "hello", 1), Token(TokenType.EOF, "", "", 1)),
          "hello",
        ),
      )

    @JvmStatic
    fun provideGroupingTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(
            Token(TokenType.LEFT_PAREN, "(", "(", 1),
            Token(TokenType.NUMBER, "42", "42", 1),
            Token(TokenType.RIGHT_PAREN, ")", ")", 1),
            Token(TokenType.EOF, "", "", 1),
          ),
          Expr.Grouping(Expr.Literal(42.0)),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.LEFT_PAREN, "(", "(", 1),
            Token(TokenType.STRING, "\"hello\"", "hello", 1),
            Token(TokenType.RIGHT_PAREN, ")", ")", 1),
            Token(TokenType.EOF, "", "", 1),
          ),
          Expr.Grouping(Expr.Literal("hello")),
        ),
      )

    @JvmStatic
    fun provideUnaryTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(
            Token(TokenType.MINUS, "-", null, 1),
            Token(TokenType.NUMBER, "42", 42.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Unary(Token(TokenType.MINUS, "-", null, 1), Expr.Literal(42.0)),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.BANG, "!", null, 1),
            Token(TokenType.TRUE, "true", true, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Unary(Token(TokenType.BANG, "!", null, 1), Expr.Literal(true)),
        ),
      )
  }

  @ParameterizedTest
  @MethodSource("provideLiteralTestCases")
  fun `parses literals correctly`(
    tokens: List<Token>,
    expectedValue: Any?,
  ) {
    val expr = Parser(tokens).parse()
    assertEquals(expectedValue, (expr as Expr.Literal).value)
  }

  @ParameterizedTest
  @MethodSource("provideGroupingTestCases")
  fun `parses grouping expressions correctly`(
    tokens: List<Token>,
    expectedExpr: Expr,
  ) {
    val expr = Parser(tokens).parse()
    assertEquals(expectedExpr, expr)
  }

  @ParameterizedTest
  @MethodSource("provideUnaryTestCases")
  fun `parses unary expressions correctly`(
    tokens: List<Token>,
    expectedExpr: Expr,
  ) {
    val expr = Parser(tokens).parse()
    assertEquals(expectedExpr, expr)
  }
}
