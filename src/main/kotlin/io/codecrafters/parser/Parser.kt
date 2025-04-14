package io.codecrafters.parser

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import io.codecrafters.model.ParseError
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType

private val UNARY_TOKEN_TYPES = setOf(TokenType.BANG, TokenType.MINUS)
private val EQUALITY_OPERATORS = setOf(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)
private val COMPARISON_OPERATORS =
  setOf(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)
private val ADDITIVE_OPERATORS = setOf(TokenType.PLUS, TokenType.MINUS)
private val MULTIPLICATIVE_OPERATORS = setOf(TokenType.STAR, TokenType.SLASH)

class Parser(
  private val tokens: List<Token>,
  private val raise: Raise<ParseError>,
) {
  private var currentIndex = 0

  fun parse(): Expr = parseExpression()

  private fun parseExpression(): Expr = parseEquality()

  private fun parseEquality(): Expr {
    var leftExpression = parseComparison()
    while (tokens.getOrNull(currentIndex)?.type in EQUALITY_OPERATORS) {
      val operatorToken = advanceToken()
      val rightExpression = parseComparison()
      leftExpression = Expr.Binary(leftExpression, operatorToken, rightExpression)
    }
    return leftExpression
  }

  private fun parseComparison(): Expr {
    var leftExpression = parseAdditive()
    while (tokens.getOrNull(currentIndex)?.type in COMPARISON_OPERATORS) {
      val operatorToken = advanceToken()
      val rightExpression = parseAdditive()
      leftExpression = Expr.Binary(leftExpression, operatorToken, rightExpression)
    }
    return leftExpression
  }

  private fun parseAdditive(): Expr {
    var leftExpression = parseMultiplicative()
    while (tokens.getOrNull(currentIndex)?.type in ADDITIVE_OPERATORS) {
      val operatorToken = advanceToken()
      val rightExpression = parseMultiplicative()
      leftExpression = Expr.Binary(leftExpression, operatorToken, rightExpression)
    }
    return leftExpression
  }

  private fun parseMultiplicative(): Expr {
    var leftExpression = parseUnary()
    while (tokens.getOrNull(currentIndex)?.type in MULTIPLICATIVE_OPERATORS) {
      val operatorToken = advanceToken()
      val rightExpression = parseUnary()
      leftExpression = Expr.Binary(leftExpression, operatorToken, rightExpression)
    }
    return leftExpression
  }

  private fun parseUnary(): Expr {
    if (tokens.getOrNull(currentIndex)?.type in UNARY_TOKEN_TYPES) {
      val operatorToken = advanceToken()
      val rightExpression = parseUnary()
      return Expr.Unary(operatorToken, rightExpression)
    }
    return parsePrimary()
  }

  private fun parsePrimary(): Expr {
    val token =
      tokens.getOrNull(currentIndex)
        ?: raise.raise(ParseError("Unexpected end of input", tokens.last()))
    return when (token.type) {
      TokenType.FALSE -> {
        advanceToken()
        Expr.Literal(false)
      }

      TokenType.TRUE -> {
        advanceToken()
        Expr.Literal(true)
      }

      TokenType.NIL -> {
        advanceToken()
        Expr.Literal(null)
      }

      TokenType.NUMBER -> {
        val numberValue = token.lexeme.toDoubleOrNull()
        raise.ensureNotNull(numberValue) { ParseError("Invalid number literal '${token.lexeme}'", token) }
        advanceToken()
        Expr.Literal(numberValue)
      }

      TokenType.STRING -> {
        val stringValue = token.lexeme.removeSurrounding("\"")
        advanceToken()
        Expr.Literal(stringValue)
      }

      TokenType.LEFT_PAREN -> {
        advanceToken()
        val innerExpression = parseExpression()
        val closingToken = tokens.getOrNull(currentIndex)
        raise.ensure(closingToken?.type == TokenType.RIGHT_PAREN) {
          ParseError(
            "Expected ')' after expression",
            closingToken ?: token,
          )
        }
        advanceToken()
        Expr.Grouping(innerExpression)
      }

      else -> raise.raise(ParseError("Expected expression, found '${token.lexeme}'", token))
    }
  }

  private fun advanceToken(): Token = tokens[currentIndex++]
}
