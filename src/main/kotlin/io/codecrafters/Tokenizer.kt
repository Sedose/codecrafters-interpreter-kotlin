package io.codecrafters

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.TokenizationResult
import org.koin.core.component.KoinComponent

class Tokenizer : KoinComponent {

  fun tokenize(input: String): TokenizationResult {
    val tokens = mutableListOf<Token>()
    val errors = mutableListOf<String>()

    for (char in input) {
      processToken(char)
        ?.let (tokens::add)
        ?: errors.add("[line 1] Error: Unexpected character: $char")
    }

    return TokenizationResult(tokens, errors)
  }

  private fun processToken(token: Char): Token? {
    return when (token) {
      '(' -> Token(TokenType.LEFT_PAREN, token.toString())
      ')' -> Token(TokenType.RIGHT_PAREN, token.toString())
      '{' -> Token(TokenType.LEFT_BRACE, token.toString())
      '}' -> Token(TokenType.RIGHT_BRACE, token.toString())
      ',' -> Token(TokenType.COMMA, token.toString())
      '.' -> Token(TokenType.DOT, token.toString())
      '-' -> Token(TokenType.MINUS, token.toString())
      '+' -> Token(TokenType.PLUS, token.toString())
      ';' -> Token(TokenType.SEMICOLON, token.toString())
      '*' -> Token(TokenType.STAR, token.toString())
      else -> null
    }
  }
}
