package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.MULTI_CHAR_TOKENS
import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.tokenizer.component.TokenProcessor

class MultiCharTokenProcessor : TokenProcessor {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean {
    if (index !in input.indices || index + 1 !in input.indices) return false
    val pair = input[index] to input[index + 1]
    return MULTI_CHAR_TOKENS.containsKey(pair)
  }

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val (first, second) = input[index] to input[index + 1]
    val tokenType = MULTI_CHAR_TOKENS[first to second]!!
    return ProcessingResult(
      token =
        Token(
          type = tokenType,
          lexeme = "$first$second",
          lineNumber = lineNumber,
        ),
      newIndex = index + 2,
      error = null,
    )
  }
}
