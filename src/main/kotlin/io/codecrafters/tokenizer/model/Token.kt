package io.codecrafters.tokenizer.model

data class Token(
  val type: TokenType,
  val lexeme: String,
  val literal: Any? = null,
)
