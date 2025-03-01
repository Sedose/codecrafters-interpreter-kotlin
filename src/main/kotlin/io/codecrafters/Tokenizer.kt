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
        ?.let(tokens::add)
        ?: errors.add("[line 1] Error: Unexpected character: $char")
    }

    return TokenizationResult(tokens, errors)
  }

  private fun processToken(token: Char): Token? =
    tokenMap[token]
      ?.let { Token(it, token.toString()) }

  private val tokenMap =
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
}
