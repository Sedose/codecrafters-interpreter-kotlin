package io.codecrafters.model

data class TokenizationResult(
  val tokens: List<Token>,
  val errors: List<String>,
)
