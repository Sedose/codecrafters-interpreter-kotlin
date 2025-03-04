package io.codecrafters.tokenizer.component

import io.codecrafters.model.MultiCharToken
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import org.koin.core.component.KoinComponent

class MultiCharTokenProcessor : KoinComponent {
  private val tokens =
    mapOf(
      '=' to MultiCharToken(TokenType.EQUAL_EQUAL, '='),
      '!' to MultiCharToken(TokenType.BANG_EQUAL, '='),
      '<' to MultiCharToken(TokenType.LESS_EQUAL, '='),
      '>' to MultiCharToken(TokenType.GREATER_EQUAL, '='),
    )

  fun process(
    char: Char,
    nextChar: Char?,
  ): Token? =
    tokens[char]?.takeIf { it.secondChar == nextChar }?.let {
      Token(it.tokenType, "$char$nextChar")
    }

  fun isMultiCharToken(char: Char): Boolean = tokens.containsKey(char)
}
