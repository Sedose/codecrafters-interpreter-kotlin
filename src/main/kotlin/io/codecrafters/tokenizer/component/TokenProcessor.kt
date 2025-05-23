package io.codecrafters.tokenizer.component

import io.codecrafters.tokenizer.TokenProcessorResult

fun interface TokenProcessor {
  fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): TokenProcessorResult
}
