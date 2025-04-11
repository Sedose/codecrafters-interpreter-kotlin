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
  @ParameterizedTest
  @MethodSource("provideTestCases")
  fun parseExpressionsCorrectly(
    tokens: List<Token>,
    expectedExpression: Expr,
  ) {
    val actualExpression = Parser(tokens).parse()
    assertEquals(expectedExpression, actualExpression)
  }

  companion object {
    @JvmStatic
    fun provideLiteralTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "42.47", "42.47", 1),
            Token(TokenType.EOF, "", "", 1),
          ),
          Expr.Literal(42.47),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.TRUE, "true", "true", 1),
            Token(TokenType.EOF, "", "", 1),
          ),
          Expr.Literal(true),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.FALSE, "false", "false", 1),
            Token(TokenType.EOF, "", "", 1),
          ),
          Expr.Literal(false),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NIL, "nil", "nil", 1),
            Token(TokenType.EOF, "", "", 1),
          ),
          Expr.Literal(null),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.STRING, "\"hello\"", "hello", 1),
            Token(TokenType.EOF, "", "", 1),
          ),
          Expr.Literal("hello"),
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
          Expr.Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Literal(42.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.BANG, "!", null, 1),
            Token(TokenType.TRUE, "true", true, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Unary(
            Token(TokenType.BANG, "!", null, 1),
            Expr.Literal(true),
          ),
        ),
      )

    @JvmStatic
    fun provideMultiplicativeTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "16", 16.0, 1),
            Token(TokenType.STAR, "*", null, 1),
            Token(TokenType.NUMBER, "38", 38.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Literal(16.0),
            Token(TokenType.STAR, "*", null, 1),
            Expr.Literal(38.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "38", 38.0, 1),
            Token(TokenType.SLASH, "/", null, 1),
            Token(TokenType.NUMBER, "58", 58.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Literal(38.0),
            Token(TokenType.SLASH, "/", null, 1),
            Expr.Literal(58.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "16", 16.0, 1),
            Token(TokenType.STAR, "*", null, 1),
            Token(TokenType.NUMBER, "38", 38.0, 1),
            Token(TokenType.SLASH, "/", null, 1),
            Token(TokenType.NUMBER, "58", 58.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Binary(
              Expr.Literal(16.0),
              Token(TokenType.STAR, "*", null, 1),
              Expr.Literal(38.0),
            ),
            Token(TokenType.SLASH, "/", null, 1),
            Expr.Literal(58.0),
          ),
        ),
      )

    @JvmStatic
    fun provideAdditiveTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "52", 52.0, 1),
            Token(TokenType.PLUS, "+", null, 1),
            Token(TokenType.NUMBER, "80", 80.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Literal(52.0),
            Token(TokenType.PLUS, "+", null, 1),
            Expr.Literal(80.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "94", 94.0, 1),
            Token(TokenType.MINUS, "-", null, 1),
            Token(TokenType.NUMBER, "36", 36.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Literal(94.0),
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Literal(36.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "52", 52.0, 1),
            Token(TokenType.PLUS, "+", null, 1),
            Token(TokenType.NUMBER, "80", 80.0, 1),
            Token(TokenType.MINUS, "-", null, 1),
            Token(TokenType.NUMBER, "94", 94.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Binary(
              Expr.Literal(52.0),
              Token(TokenType.PLUS, "+", null, 1),
              Expr.Literal(80.0),
            ),
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Literal(94.0),
          ),
        ),
      )

    @JvmStatic
    fun provideTestCases(): Stream<Arguments> =
      Stream.concat(
        provideLiteralTestCases(),
        Stream.concat(
          provideGroupingTestCases(),
          Stream.concat(
            provideUnaryTestCases(),
            Stream.concat(
              provideMultiplicativeTestCases(),
              provideAdditiveTestCases(),
            ),
          ),
        ),
      )
  }
}
