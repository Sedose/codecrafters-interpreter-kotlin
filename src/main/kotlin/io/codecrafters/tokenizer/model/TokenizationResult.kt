package io.codecrafters.tokenizer.model

data class TokenizationResult(
  val tokens: List<Token>,
  val errors: List<String>,
)
