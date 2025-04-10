package io.codecrafters.tokenizer.model

import io.codecrafters.model.TokenType

data class Token(
  val type: TokenType,
  val lexeme: String,
  val literal: Any? = null,
  val lineNumber: Int,
)
