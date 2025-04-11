package io.codecrafters

import arrow.core.Either
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.parser.Expr
import io.codecrafters.parser.Parser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
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
    when (val result = Parser(tokens).parse()) {
      is Either.Right -> assertEquals(expectedExpression, result.value)
      is Either.Left -> fail("Expected successful parse, but got error: ${result.value}")
    }
  }

  companion object {
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
              Stream.concat(
                provideAdditiveTestCases(),
                provideComparisonTestCases(),
              ),
            ),
          ),
        ),
      )

    @JvmStatic
    fun provideLiteralTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(Token(TokenType.NUMBER, "42.47", "42.47", 1), Token(TokenType.EOF, "", "", 1)),
          Expr.Literal(42.47),
        ),
        Arguments.of(
          listOf(Token(TokenType.TRUE, "true", "true", 1), Token(TokenType.EOF, "", "", 1)),
          Expr.Literal(true),
        ),
        Arguments.of(
          listOf(Token(TokenType.FALSE, "false", "false", 1), Token(TokenType.EOF, "", "", 1)),
          Expr.Literal(false),
        ),
        Arguments.of(
          listOf(Token(TokenType.NIL, "nil", "nil", 1), Token(TokenType.EOF, "", "", 1)),
          Expr.Literal(null),
        ),
        Arguments.of(
          listOf(Token(TokenType.STRING, "\"hello\"", "hello", 1), Token(TokenType.EOF, "", "", 1)),
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
      )

    @JvmStatic
    fun provideMultiplicativeTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "3", 3.0, 1),
            Token(TokenType.STAR, "*", null, 1),
            Token(TokenType.NUMBER, "4", 4.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(Expr.Literal(3.0), Token(TokenType.STAR, "*", null, 1), Expr.Literal(4.0)),
        ),
      )

    @JvmStatic
    fun provideAdditiveTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "1", 1.0, 1),
            Token(TokenType.PLUS, "+", null, 1),
            Token(TokenType.NUMBER, "2", 2.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(Expr.Literal(1.0), Token(TokenType.PLUS, "+", null, 1), Expr.Literal(2.0)),
        ),
      )

    @JvmStatic
    fun provideComparisonTestCases(): Stream<Arguments> =
      Stream.of(
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "5", 5.0, 1),
            Token(TokenType.LESS, "<", null, 1),
            Token(TokenType.NUMBER, "10", 10.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Literal(5.0),
            Token(TokenType.LESS, "<", null, 1),
            Expr.Literal(10.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "8", 8.0, 1),
            Token(TokenType.LESS_EQUAL, "<=", null, 1),
            Token(TokenType.NUMBER, "9", 9.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Literal(8.0),
            Token(TokenType.LESS_EQUAL, "<=", null, 1),
            Expr.Literal(9.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "11", 11.0, 1),
            Token(TokenType.GREATER, ">", null, 1),
            Token(TokenType.NUMBER, "7", 7.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Literal(11.0),
            Token(TokenType.GREATER, ">", null, 1),
            Expr.Literal(7.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "15", 15.0, 1),
            Token(TokenType.GREATER_EQUAL, ">=", null, 1),
            Token(TokenType.NUMBER, "15", 15.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Literal(15.0),
            Token(TokenType.GREATER_EQUAL, ">=", null, 1),
            Expr.Literal(15.0),
          ),
        ),
        Arguments.of(
          listOf(
            Token(TokenType.NUMBER, "1", 1.0, 1),
            Token(TokenType.LESS, "<", null, 1),
            Token(TokenType.NUMBER, "2", 2.0, 1),
            Token(TokenType.LESS, "<", null, 1),
            Token(TokenType.NUMBER, "3", 3.0, 1),
            Token(TokenType.EOF, "", null, 1),
          ),
          Expr.Binary(
            Expr.Binary(
              Expr.Literal(1.0),
              Token(TokenType.LESS, "<", null, 1),
              Expr.Literal(2.0),
            ),
            Token(TokenType.LESS, "<", null, 1),
            Expr.Literal(3.0),
          ),
        ),
      )
  }
}
