package io.codecrafters.model

import io.codecrafters.TokenType

data class Token(
  val type: TokenType,
  val lexeme: String,
  val literal: Any? = null,
)
