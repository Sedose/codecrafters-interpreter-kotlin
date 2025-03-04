package io.codecrafters.tokenizer.component

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import org.koin.core.component.KoinComponent

class SingleCharTokenProcessor : KoinComponent {
  private val tokens =
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
      '/' to TokenType.SLASH,
      '=' to TokenType.EQUAL,
      '!' to TokenType.BANG,
      '<' to TokenType.LESS,
      '>' to TokenType.GREATER,
    )

  fun canProcess(char: Char): Boolean = char in tokens

  fun process(char: Char): Token = Token(tokens[char]!!, char.toString())
}
