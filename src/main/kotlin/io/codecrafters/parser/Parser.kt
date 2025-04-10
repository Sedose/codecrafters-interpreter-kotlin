package io.codecrafters.parser

import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.model.Token

class Parser(
  private val tokens: List<Token>,
) {
  private var current = 0

  fun parse(): Expr = expression()

  private fun expression(): Expr = literal()

  private fun literal(): Expr =
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
      else -> throw error(peek(), "Expected literal.")
    }

  private fun advance(): Token =
    peek().also {
      if (!isAtEnd()) current++
    }

  private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

  private fun peek(): Token = tokens[current]

  private fun error(
    token: Token,
    message: String,
  ): RuntimeException {
    System.err.println("[line number: ${token.lineNumber}] Error at '${token.lexeme}': $message")
    return RuntimeException(message)
  }
}
