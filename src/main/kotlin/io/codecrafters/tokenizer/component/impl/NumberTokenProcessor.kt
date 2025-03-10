package io.codecrafters.tokenizer.component.impl

import io.codecrafters.isNumberChar
import io.codecrafters.tokenizer.component.TokenProcessor
import io.codecrafters.tokenizer.model.ProcessingResult
import io.codecrafters.tokenizer.model.Token
import io.codecrafters.tokenizer.model.TokenType
import org.koin.core.component.KoinComponent

class NumberTokenProcessor :
  TokenProcessor,
  KoinComponent {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = index in input.indices && input[index].isDigit()

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    var currentIndex = index
    var decimalPointCount = 0

    while (currentIndex in input.indices && input[currentIndex].isNumberChar()) {
      if (input[currentIndex] == '.') {
        decimalPointCount++
        if (decimalPointCount > 1) {
          return ProcessingResult(
            token = null,
            newIndex = currentIndex,
            error = "[line $lineNumber] Error: Unexpected character: .",
          )
        }
      }
      currentIndex++
    }

    val lexeme = input.substring(index, currentIndex)
    val numericValue = lexeme.toDoubleOrNull()

    return ProcessingResult(
      token = Token(TokenType.NUMBER, lexeme, numericValue),
      newIndex = currentIndex,
      error = null,
    )
  }
}
