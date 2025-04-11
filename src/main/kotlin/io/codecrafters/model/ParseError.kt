package io.codecrafters.model

data class ParseError(
  val message: String,
  val token: Token,
)
