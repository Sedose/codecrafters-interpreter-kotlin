package io.codecrafters.tokenizer.component

import io.codecrafters.model.IdentifierProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import org.koin.core.component.KoinComponent

class IdentifierProcessor : KoinComponent {
  fun processIdentifierOrKeyword(
    input: String,
    startIndex: Int,
  ): IdentifierProcessingResult {
    var index = startIndex
    while (index < input.length && (input[index].isLetterOrDigit() || input[index] == '_')) {
      index++
    }

    val lexeme = input.substring(startIndex, index)
    val tokenType = reservedWords[lexeme] ?: TokenType.IDENTIFIER
    return IdentifierProcessingResult(Token(tokenType, lexeme), index)
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
