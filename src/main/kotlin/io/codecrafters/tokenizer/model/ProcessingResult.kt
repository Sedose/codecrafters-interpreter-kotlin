package io.codecrafters.tokenizer.model

data class ProcessingResult(
  val token: Token?,
  val newIndex: Int,
  val error: String?,
)
