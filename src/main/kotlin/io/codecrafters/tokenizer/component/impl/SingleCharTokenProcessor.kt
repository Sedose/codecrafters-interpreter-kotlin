package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.SINGLE_CHAR_TOKENS
import io.codecrafters.model.Token
import io.codecrafters.tokenizer.TokenProcessorResult
import io.codecrafters.tokenizer.component.TokenProcessor

class SingleCharTokenProcessor : TokenProcessor {
  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): TokenProcessorResult {
    val tokenType = SINGLE_CHAR_TOKENS[input[index]] ?: return TokenProcessorResult.Skipped(index)
    val token = Token(tokenType, input[index].toString(), null, lineNumber)
    return TokenProcessorResult.Produced(token, index + 1)
  }
}
