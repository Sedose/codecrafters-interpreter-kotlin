package io.codecrafters.parser

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import io.codecrafters.model.ParseError
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType

private val UNARY_TOKEN_TYPES = setOf(TokenType.BANG, TokenType.MINUS)
private val EQUALITY_OPERATORS = setOf(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)
private val COMPARISON_OPERATORS = setOf(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)
private val ADDITIVE_OPERATORS = setOf(TokenType.PLUS, TokenType.MINUS)
private val MULTIPLICATIVE_OPERATORS = setOf(TokenType.STAR, TokenType.SLASH)

data class ExpressionResult(
  val expression: Expr,
  val nextIndex: Int,
)

class Parser(
  private val tokens: List<Token>,
  private val raise: Raise<ParseError>,
) {
  private var currentIndex: Int = 0

  fun parse(): Expr {
    val (expression, _) = parseExpression()
    return expression
  }

  private fun parseExpression(): ExpressionResult = parseEquality()

  private fun parseEquality(): ExpressionResult = parseBinary(::parseComparison, EQUALITY_OPERATORS)

  private fun parseComparison(): ExpressionResult = parseBinary(::parseAdditive, COMPARISON_OPERATORS)

  private fun parseAdditive(): ExpressionResult = parseBinary(::parseMultiplicative, ADDITIVE_OPERATORS)

  private fun parseMultiplicative(): ExpressionResult = parseBinary(::parseUnary, MULTIPLICATIVE_OPERATORS)

  private fun parseBinary(
    parseNext: () -> ExpressionResult,
    operators: Set<TokenType>,
  ): ExpressionResult {
    var (left, index) = parseNext()

    while (index in tokens.indices && tokens[index].type in operators) {
      val operator = tokens[index]
      currentIndex = index + 1
      val (right, nextIndex) = parseNext()
      left = Expr.Binary(left, operator, right)
      index = nextIndex
    }

    return ExpressionResult(left, index)
  }

  private fun parseUnary(): ExpressionResult {
    if (currentIndex in tokens.indices && tokens[currentIndex].type in UNARY_TOKEN_TYPES) {
      val operator = tokens[currentIndex++]
      val (right, nextIndex) = parseUnary()
      return ExpressionResult(Expr.Unary(operator, right), nextIndex)
    }
    return parsePrimary()
  }

  private fun parsePrimary(): ExpressionResult {
    val token =
      tokens.getOrNull(currentIndex)
        ?: raise.raise(ParseError("Unexpected end of input", tokens.last()))

    return when (token.type) {
      TokenType.FALSE -> ExpressionResult(Expr.Literal(false), ++currentIndex)
      TokenType.TRUE -> ExpressionResult(Expr.Literal(true), ++currentIndex)
      TokenType.NIL -> ExpressionResult(Expr.Literal(null), ++currentIndex)
      TokenType.NUMBER -> {
        val value = token.lexeme.toDoubleOrNull()
        raise.ensureNotNull(value) { ParseError("Invalid number literal '${token.lexeme}'", token) }
        ExpressionResult(Expr.Literal(value), ++currentIndex)
      }
      TokenType.STRING -> {
        val value = token.lexeme.removeSurrounding("\"")
        ExpressionResult(Expr.Literal(value), ++currentIndex)
      }
      TokenType.LEFT_PAREN -> {
        currentIndex++
        val (expression, _) = parseExpression()
        val closing = tokens.getOrNull(currentIndex)
        raise.ensure(closing?.type == TokenType.RIGHT_PAREN) {
          ParseError("Expected ')' after expression", closing ?: token)
        }
        currentIndex++
        ExpressionResult(Expr.Grouping(expression), currentIndex)
      }
      else -> raise.raise(ParseError("Expected expression, found '${token.lexeme}'", token))
    }
  }
}
