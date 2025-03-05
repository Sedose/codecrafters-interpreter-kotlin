package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor
import org.koin.core.component.KoinComponent

class IdentifierProcessor :
  TokenProcessor,
  KoinComponent {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = index < input.length && (input[index].isLetter() || input[index] == '_')

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    var current = index
    while (
      current < input.length &&
      (input[current].isLetterOrDigit() || input[current] == '_')
    ) {
      current++
    }
    val lexeme = input.substring(index, current)
    val type = reservedWords[lexeme] ?: TokenType.IDENTIFIER
    val token = Token(type, lexeme)
    return ProcessingResult(token, current, null)
  }

  private val reservedWords =
    mapOf(
      "and" to TokenType.AND,
      "class" to TokenType.CLASS,
      "else" to TokenType.ELSE,
      "false" to TokenType.FALSE,
      "for" to TokenType.FOR,
      "fun" to TokenType.FUN,
      "if" to TokenType.IF,
      "nil" to TokenType.NIL,
      "or" to TokenType.OR,
      "print" to TokenType.PRINT,
      "return" to TokenType.RETURN,
      "super" to TokenType.SUPER,
      "this" to TokenType.THIS,
      "true" to TokenType.TRUE,
      "var" to TokenType.VAR,
      "while" to TokenType.WHILE,
    )
}
