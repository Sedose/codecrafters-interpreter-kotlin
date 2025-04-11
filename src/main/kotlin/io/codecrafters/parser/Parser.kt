package io.codecrafters.parser

import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.model.Token

class Parser(
  private val tokens: List<Token>,
) {
  private var current = 0

  fun parse(): Expr = expression()

  private fun expression(): Expr = additive()

  private fun additive(): Expr {
    var expr = multiplicative()

    while (match(TokenType.PLUS, TokenType.MINUS)) {
      val operator = previous()
      val right = multiplicative()
      expr = Expr.Binary(expr, operator, right)
    }

    return expr
  }

  private fun multiplicative(): Expr {
    var expr = unary()

    while (match(TokenType.STAR, TokenType.SLASH)) {
      val operator = previous()
      val right = unary()
      expr = Expr.Binary(expr, operator, right)
    }

    return expr
  }

  private fun unary(): Expr =
    if (match(TokenType.BANG, TokenType.MINUS)) {
      val operator = previous()
      val right = unary()
      Expr.Unary(operator, right)
    } else {
      primary()
    }

  private fun primary(): Expr =
    when (peek().type) {
      TokenType.FALSE -> {
        advance()
        Expr.Literal(false)
      }
      TokenType.TRUE -> {
        advance()
        Expr.Literal(true)
      }
      TokenType.NIL -> {
        advance()
        Expr.Literal(null)
      }
      TokenType.NUMBER -> {
        val value = peek().lexeme.toDouble()
        advance()
        Expr.Literal(value)
      }
      TokenType.STRING -> {
        val value = peek().lexeme.removeSurrounding("\"")
        advance()
        Expr.Literal(value)
      }
      TokenType.LEFT_PAREN -> {
        advance()
        val expr = expression()
        consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.")
        Expr.Grouping(expr)
      }
      else -> throw error(peek(), "Expected expression.")
    }

  private fun advance(): Token =
    peek().also {
      if (!isAtEnd()) current++
    }

  private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

  private fun peek(): Token = tokens[current]

  private fun match(vararg types: TokenType): Boolean {
    for (type in types) {
      if (check(type)) {
        advance()
        return true
      }
    }
    return false
  }

  private fun check(type: TokenType): Boolean = if (isAtEnd()) false else peek().type == type

  private fun consume(
    type: TokenType,
    message: String,
  ): Token = if (check(type)) advance() else throw error(peek(), message)

  private fun error(
    token: Token,
    message: String,
  ): RuntimeException {
    System.err.println("[line number: ${token.lineNumber}] Error at '${token.lexeme}': $message")
    return RuntimeException(message)
  }

  private fun previous(): Token = tokens[current - 1]
}
