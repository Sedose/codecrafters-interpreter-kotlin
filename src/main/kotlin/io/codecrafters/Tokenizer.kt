package io.codecrafters

import io.codecrafters.model.Token
import io.codecrafters.model.TokenizationResult
import org.koin.core.component.KoinComponent

class Tokenizer : KoinComponent {

  private val tokenToType =
    mapOf(
      '(' to TokenType.LEFT_PAREN,
      ')' to TokenType.RIGHT_PAREN,
      '{' to TokenType.LEFT_BRACE,
      '}' to TokenType.RIGHT_BRACE,
      ',' to TokenType.COMMA,
      '.' to TokenType.DOT,
      '-' to TokenType.MINUS,
      '+' to TokenType.PLUS,
      ';' to TokenType.SEMICOLON,
      '*' to TokenType.STAR,
    )

  fun tokenize(input: String): TokenizationResult {
    val tokens = mutableListOf<Token>()
    val errors = mutableListOf<String>()

    for (char in input) {
      val token = processToken(char)
      if (token != null) {
        tokens.add(token)
      } else {
        errors.add("[line 1] Error: Unexpected character: $char")
      }
    }

    return TokenizationResult(tokens, errors)
  }

  private fun processToken(token: Char): Token? {
    return if (token in tokenToType) {
      Token(tokenToType[token]!!, token.toString(), null)
    } else {
      null
    }
  }
}
