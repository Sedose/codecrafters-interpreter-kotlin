package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor
import org.koin.core.component.KoinComponent

class StringTokenProcessor :
  TokenProcessor,
  KoinComponent {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = index <= input.lastIndex && input[index] == '"'

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val end =
      (index + 1 until input.length)
        .find { input[it] == '"' || input[it] == '\n' }
        ?: input.length
    if (end > input.lastIndex || input[end] == '\n') {
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
