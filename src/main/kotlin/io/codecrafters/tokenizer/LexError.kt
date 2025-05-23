package io.codecrafters.tokenizer

data class LexError(
  val lineNumber: Int,
  val message: String,
) {
  override fun toString(): String = "[line $lineNumber] $message"
}
