package io.codecrafters.parser

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import io.codecrafters.model.ParseError
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType

class Parser(
  private val tokens: List<Token>,
) {
  private var currentTokenIndex = 0

  fun parse(): Either<ParseError, Expr> =
    either {
      parseExpression(this)
    }

  private fun parseExpression(raise: Raise<ParseError>): Expr = parseComparison(raise)

  private fun parseComparison(raise: Raise<ParseError>): Expr {
    var expression = parseAdditive(raise)

    while (
      match(
        TokenType.GREATER,
        TokenType.GREATER_EQUAL,
        TokenType.LESS,
        TokenType.LESS_EQUAL,
      )
    ) {
      val operator = previousToken()
      val right = parseAdditive(raise)
      expression = Expr.Binary(expression, operator, right)
    }

    return expression
  }

  private fun parseAdditive(raise: Raise<ParseError>): Expr {
    var expression = parseMultiplicative(raise)

    while (match(TokenType.PLUS, TokenType.MINUS)) {
      val operator = previousToken()
      val right = parseMultiplicative(raise)
      expression = Expr.Binary(expression, operator, right)
    }

    return expression
  }

  private fun parseMultiplicative(raise: Raise<ParseError>): Expr {
    var expression = parseUnary(raise)

    while (match(TokenType.STAR, TokenType.SLASH)) {
      val operator = previousToken()
      val right = parseUnary(raise)
      expression = Expr.Binary(expression, operator, right)
    }

    return expression
  }

  private fun parseUnary(raise: Raise<ParseError>): Expr =
    if (match(TokenType.BANG, TokenType.MINUS)) {
      val operator = previousToken()
      val right = parseUnary(raise)
      Expr.Unary(operator, right)
    } else {
      parsePrimary(raise)
    }

  private fun parsePrimary(raise: Raise<ParseError>): Expr {
    val token = peek()
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
        val value = token.lexeme.toDoubleOrNull()
        raise.ensureNotNull(value) { ParseError("Invalid number literal", token) }
        advanceToken()
        Expr.Literal(value)
      }
      TokenType.STRING -> {
        val value = token.lexeme.removeSurrounding("\"")
        advanceToken()
        Expr.Literal(value)
      }
      TokenType.LEFT_PAREN -> {
        advanceToken()
        val expression = parseExpression(raise)
        raise.ensure(check(TokenType.RIGHT_PAREN)) {
          ParseError("Expected ')' after expression", peek())
        }
        advanceToken()
        Expr.Grouping(expression)
      }
      else -> raise.raise(ParseError("Expected expression", token))
    }
  }

  private fun advanceToken(): Token =
    peek().also {
      if (!isAtEnd()) currentTokenIndex++
    }

  private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

  private fun peek(): Token = tokens[currentTokenIndex]

  private fun match(vararg types: TokenType): Boolean {
    for (type in types) {
      if (check(type)) {
        advanceToken()
        return true
      }
    }
    return false
  }

  private fun check(type: TokenType): Boolean = if (isAtEnd()) false else peek().type == type

  private fun previousToken(): Token = tokens[currentTokenIndex - 1]
}
