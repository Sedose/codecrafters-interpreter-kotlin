package io.codecrafters.tokenizer.component.impl

import io.codecrafters.isIdentifierChar
import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.RESERVED_WORDS
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
  ): Boolean = index in input.indices && input[index].isIdentifierChar()

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    var newIndex = index
    while (newIndex in input.indices && input[newIndex].isIdentifierChar()) {
      newIndex++
    }
    val lexeme = input.substring(index, newIndex)
    return ProcessingResult(
      token =
        Token(
          type = RESERVED_WORDS[lexeme] ?: TokenType.IDENTIFIER,
          lexeme = lexeme,
        ),
      newIndex = newIndex,
      error = null,
    )
  }
}
