package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor
import org.koin.core.component.KoinComponent

class SingleCharTokenProcessor :
  TokenProcessor,
  KoinComponent {
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

  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = index < input.length && tokens.containsKey(input[index])

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val type = tokens[input[index]]!!
    val token = Token(type, input[index].toString())
    return ProcessingResult(token, index + 1, null)
  }
}
