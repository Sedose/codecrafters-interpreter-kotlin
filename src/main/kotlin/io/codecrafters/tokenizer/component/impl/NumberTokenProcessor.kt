package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.LexError
import io.codecrafters.tokenizer.TokenProcessorResult
import io.codecrafters.tokenizer.component.TokenProcessor

class NumberTokenProcessor : TokenProcessor {
  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): TokenProcessorResult {
    val first = input.getOrNull(index) ?: return TokenProcessorResult.Skipped(index)
    if (!first.isDigit()) return TokenProcessorResult.Skipped(index)
    var current = index
    var dotCount = 0
    while (current <= input.lastIndex) {
      val currentChar = input[current]
      if (currentChar.isDigit()) {
        current += 1
        continue
      }
      if (currentChar == '.') {
        dotCount += 1
        if (dotCount > 1) return TokenProcessorResult.Error(LexError(lineNumber, "Error: Unexpected character: ."), current)
        current += 1
        continue
      }
      break
    }
    val lexeme = input.substring(index, current)
    val numberValue =
      lexeme.toDoubleOrNull() ?: return TokenProcessorResult.Error(LexError(lineNumber, "Error: Invalid number format."), current)
    val token = Token(TokenType.NUMBER, lexeme, numberValue, lineNumber)
    return TokenProcessorResult.Produced(token, current)
  }
}
