package io.codecrafters

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

  var hasError = false  // Track if errors exist

  fun processToken(token: Char): String? {
    return if (token in tokenToType) {
      "${tokenToType[token]} $token null"
    } else {
      System.err.println("[line 1] Error: Unexpected character: $token")
      hasError = true
      null
    }
  }
}
