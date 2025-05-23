package io.codecrafters.tokenizer.component.impl

import io.codecrafters.tokenizer.TokenProcessorResult
import io.codecrafters.tokenizer.component.TokenProcessor

class SingleLineCommentSkipper : TokenProcessor {
  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): TokenProcessorResult {
    if (input.getOrNull(index) == '/' && input.getOrNull(index + 1) == '/') {
      var current = index + 2
      while (current <= input.lastIndex && input[current] != '\n') {
        current += 1
      }
      return TokenProcessorResult.Skipped(current)
    }
    return TokenProcessorResult.Skipped(index)
  }
}
