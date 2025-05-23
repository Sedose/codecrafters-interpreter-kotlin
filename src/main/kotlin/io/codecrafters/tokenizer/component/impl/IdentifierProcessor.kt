package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.RESERVED_WORDS
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.TokenProcessorResult
import io.codecrafters.tokenizer.component.TokenProcessor

class IdentifierProcessor : TokenProcessor {
  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): TokenProcessorResult {
    val first = input.getOrNull(index) ?: return TokenProcessorResult.Skipped(index)
    if (!first.isLetterOrDigit() && first != '_') return TokenProcessorResult.Skipped(index)
    var current = index
    while (current <= input.lastIndex && (input[current].isLetterOrDigit() || input[current] == '_')) {
      current += 1
    }
    val lexeme = input.substring(index, current)
    val tokenType = RESERVED_WORDS[lexeme] ?: TokenType.IDENTIFIER
    val token = Token(tokenType, lexeme, null, lineNumber)
    return TokenProcessorResult.Produced(token, current)
  }
}
