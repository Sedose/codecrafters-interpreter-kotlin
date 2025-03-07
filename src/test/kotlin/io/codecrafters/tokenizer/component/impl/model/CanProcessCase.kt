package io.codecrafters.tokenizer.component.impl.model

data class CanProcessCase(
  val input: String,
  val startIndex: Int,
  val expectCanProcess: Boolean,
)
