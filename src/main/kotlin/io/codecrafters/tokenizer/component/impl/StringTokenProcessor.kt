package io.codecrafters.tokenizer.component.impl

import io.codecrafters.tokenizer.component.TokenProcessor
import io.codecrafters.tokenizer.model.ProcessingResult
import io.codecrafters.tokenizer.model.Token
import io.codecrafters.tokenizer.model.TokenType
import org.koin.core.component.KoinComponent

private val STRING_TERMINATORS = charArrayOf('"', '\n')

class StringTokenProcessor :
  TokenProcessor,
  KoinComponent {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = index in input.indices && input[index] == '"'

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
    if (end !in input.indices || input[end] == '\n') {
      return ProcessingResult(null, end, "[line $lineNumber] Error: Unterminated string.")
    }
    return ProcessingResult(
      token =
        Token(
          TokenType.STRING,
          input.substring(index, end + 1),
          input.substring(index + 1, end),
        ),
      newIndex = end + 1,
      error = null,
    )
  }
}
