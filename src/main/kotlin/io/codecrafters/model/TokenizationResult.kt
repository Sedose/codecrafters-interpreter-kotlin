package io.codecrafters.model

import io.codecrafters.tokenizer.LexError

data class TokenizationResult(
  val tokens: List<Token>,
  val errors: List<LexError>,
)
