package io.codecrafters.tokenizer.component.impl

import io.codecrafters.isAfter
import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor
import org.springframework.stereotype.Component

@Component
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
    var current = index + 1
    while (current <= input.lastIndex && input[current] != '"') {
      current += 1
    }

    if (current isAfter input.lastIndex) {
      return ProcessingResult(
        token = null,
        newIndex = current,
        error = "[line $lineNumber] Error: Unterminated string.",
      )
    }

    val lexeme = input.substring(index, current + 1)
    return ProcessingResult(
      token =
        Token(
          type = TokenType.STRING,
          lexeme = lexeme,
          literal = lexeme.substring(1, lexeme.lastIndex),
          lineNumber = lineNumber,
        ),
      newIndex = current + 1,
      error = null,
    )
  }
}
