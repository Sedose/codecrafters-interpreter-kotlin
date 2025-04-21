package io.codecrafters.tokenizer.component.impl

import io.codecrafters.isAfter
import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor

private val STRING_TERMINATORS = charArrayOf('"', '\n')

class StringTokenProcessor : TokenProcessor {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = input.getOrNull(index) == '"'

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val end =
      input
        .indexOfAny(STRING_TERMINATORS, startIndex = index + 1)
        .takeUnless { it == -1 }
        ?: input.length
    if (end isAfter input.lastIndex || input[end] == '\n') {
      return ProcessingResult(null, end, "[line $lineNumber] Error: Unterminated string.")
    }
    return ProcessingResult(
      token =
        Token(
          TokenType.STRING,
          input.substring(index, end + 1),
          input.substring(index + 1, end),
          lineNumber,
        ),
      newIndex = end + 1,
      error = null,
    )
  }
}
