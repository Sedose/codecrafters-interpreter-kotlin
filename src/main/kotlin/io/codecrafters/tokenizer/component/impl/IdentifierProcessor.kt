package io.codecrafters.tokenizer.component.impl

import io.codecrafters.isIdentifierChar
import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.RESERVED_WORDS
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor

class IdentifierProcessor : TokenProcessor {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = input.getOrNull(index).isIdentifierChar()

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val lexeme = extractLexeme(input, index)
    return ProcessingResult(Token(RESERVED_WORDS[lexeme] ?: TokenType.IDENTIFIER, lexeme, null, lineNumber), index + lexeme.length, null)
  }

  private fun extractLexeme(
    input: String,
    start: Int,
  ): String = input.substring(start, start + input.drop(start).takeWhile { it.isIdentifierChar() }.length)
}
