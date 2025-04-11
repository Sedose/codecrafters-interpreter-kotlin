package io.codecrafters.model

data class Token(
  val type: TokenType,
  val lexeme: String,
  val literal: Any? = null,
  val lineNumber: Int,
)
