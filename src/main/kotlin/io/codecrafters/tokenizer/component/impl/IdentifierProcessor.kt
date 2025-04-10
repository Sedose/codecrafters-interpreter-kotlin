package io.codecrafters.tokenizer.component.impl

import io.codecrafters.isIdentifierChar
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor
import io.codecrafters.tokenizer.model.ProcessingResult
import io.codecrafters.tokenizer.model.RESERVED_WORDS
import io.codecrafters.tokenizer.model.Token
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
    val lexeme = extractLexeme(input, index)
    return ProcessingResult(Token(RESERVED_WORDS[lexeme] ?: TokenType.IDENTIFIER, lexeme), index + lexeme.length, null)
  }

  private fun extractLexeme(
    input: String,
    start: Int,
  ): String = input.substring(start, start + input.drop(start).takeWhile { it.isIdentifierChar() }.length)
}
