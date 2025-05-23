package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.MULTI_CHAR_TOKENS
import io.codecrafters.model.Token
import io.codecrafters.tokenizer.TokenProcessorResult
import io.codecrafters.tokenizer.component.TokenProcessor

class MultiCharTokenProcessor : TokenProcessor {
  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): TokenProcessorResult {
    val next = input.getOrNull(index + 1) ?: return TokenProcessorResult.Skipped(index)
    val tokenType = MULTI_CHAR_TOKENS[input[index] to next] ?: return TokenProcessorResult.Skipped(index)
    val token = Token(tokenType, "${input[index]}$next", null, lineNumber)
    return TokenProcessorResult.Produced(token, index + 2)
  }
}
