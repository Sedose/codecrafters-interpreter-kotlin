package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor
import org.koin.core.component.KoinComponent

class NumberTokenProcessor :
  TokenProcessor,
  KoinComponent {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = index < input.length && input[index].isDigit()

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    var currentIndex = index

    while (
      currentIndex < input.length &&
      (input[currentIndex].isDigit() || input[currentIndex] == '.')
    ) {
      currentIndex++
    }

    val lexeme = input.substring(index, currentIndex)

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
