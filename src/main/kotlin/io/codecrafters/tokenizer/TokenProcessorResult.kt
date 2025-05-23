package io.codecrafters.tokenizer

import io.codecrafters.model.Token

sealed interface TokenProcessorResult {
  data class Produced(
    val token: Token,
    val newIndex: Int,
  ) : TokenProcessorResult

  data class Skipped(
    val newIndex: Int,
  ) : TokenProcessorResult

  data class Error(
    val error: LexError,
    val newIndex: Int,
  ) : TokenProcessorResult
}
