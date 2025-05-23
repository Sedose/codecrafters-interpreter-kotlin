package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.LexError
import io.codecrafters.tokenizer.TokenProcessorResult
import io.codecrafters.tokenizer.component.TokenProcessor

class StringTokenProcessor : TokenProcessor {
  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): TokenProcessorResult {
    if (input.getOrNull(index) != '"') return TokenProcessorResult.Skipped(index)
    var current = index + 1
    while (current <= input.lastIndex && input[current] != '"') {
      current += 1
    }
    if (current > input.lastIndex) {
      return TokenProcessorResult.Error(
        LexError(
          lineNumber,
          "Error: Unterminated string.",
        ),
        current,
      )
    }
    val lexeme = input.substring(index, current + 1)
    val token = Token(TokenType.STRING, lexeme, lexeme.substring(1, lexeme.lastIndex), lineNumber)
    return TokenProcessorResult.Produced(token, current + 1)
  }
}
