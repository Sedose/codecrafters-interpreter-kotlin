package io.codecrafters.tokenizer.component

import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import org.springframework.stereotype.Component

@Component
class StringTokenProcessor {
  fun processString(
    input: String,
    startIndex: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val endIndex =
      (startIndex + 1 until input.length)
        .find { input[it] == '"' || input[it] == '\n' }
        ?: input.length

    return when {
      endIndex >= input.length || input[endIndex] == '\n' ->
        ProcessingResult(null, endIndex, "[line $lineNumber] Error: Unterminated string.")
      else ->
        ProcessingResult(
          Token(
            TokenType.STRING,
            input.substring(startIndex, endIndex + 1),
            input.substring(startIndex + 1, endIndex),
          ),
          endIndex + 1,
          null,
        )
    }
  }
}
