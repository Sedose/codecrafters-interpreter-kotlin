package io.codecrafters.tokenizer.component.impl.model

import io.codecrafters.model.TokenType

data class ProcessCase(
  val input: String,
  val startIndex: Int,
  val expectedType: TokenType,
  val expectedLexeme: String,
  val expectedNewIndex: Int,
)
