package io.codecrafters.tokenizer.component

import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import org.springframework.stereotype.Component

@Component
class NumberTokenProcessor {
  fun processNumber(
    input: String,
    startIndex: Int,
    lineNumber: Int,
  ): ProcessingResult {
    var currentIndex = startIndex

    while (
      currentIndex < input.length &&
      (input[currentIndex].isDigit() || input[currentIndex] == '.')
    ) {
      currentIndex++
    }

    val lexeme = input.substring(startIndex, currentIndex)

    if (lexeme.count { it == '.' } > 1) {
      return ProcessingResult(
        token = null,
        newIndex = currentIndex,
        error = "[line $lineNumber] Error: Unexpected character: .",
      )
    }

    val numericValue = lexeme.toDoubleOrNull()
    return ProcessingResult(
      token = Token(TokenType.NUMBER, lexeme, numericValue),
      newIndex = currentIndex,
      error = null,
    )
  }
}
