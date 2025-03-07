package io.codecrafters.tokenizer.component.impl.model

import io.codecrafters.model.TokenType

data class SuccessProcessCase(
  val input: String,
  val startIndex: Int,
  val expectedNewIndex: Int,
  val expectedType: TokenType,
  val expectedLexeme: String,
  val expectedValue: Any? = null,
)

data class ErrorProcessCase(
  val input: String,
  val startIndex: Int,
  val expectedNewIndex: Int,
  val expectedError: String,
)
