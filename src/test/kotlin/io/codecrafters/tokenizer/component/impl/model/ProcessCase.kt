package io.codecrafters.tokenizer.component.impl.model

import io.codecrafters.model.TokenType

sealed interface ProcessCase {
  val input: String
  val startIndex: Int
  val expectedNewIndex: Int
}

data class SuccessProcessCase(
  override val input: String,
  override val startIndex: Int,
  override val expectedNewIndex: Int,
  val expectedType: TokenType,
  val expectedLexeme: String,
  val expectedValue: Any? = null,
) : ProcessCase

data class ErrorProcessCase(
  override val input: String,
  override val startIndex: Int,
  override val expectedNewIndex: Int,
  val expectedError: String,
) : ProcessCase
