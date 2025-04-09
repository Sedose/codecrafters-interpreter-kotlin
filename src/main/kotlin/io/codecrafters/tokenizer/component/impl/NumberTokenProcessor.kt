package io.codecrafters.tokenizer.component.impl

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

  @Suppress("ReturnCount")
  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val remainingInput = input.substring(index)
    val regex = Regex("^\\d[.\\d]*")
    val match =
      regex.find(remainingInput)
        ?: return ProcessingResult(
          token = null,
          newIndex = index,
          error = "[line $lineNumber] Error: Invalid number format.",
        )

    val lexemeCandidate = match.value
    val dotCount = lexemeCandidate.count { it == '.' }

    if (dotCount > 1) {
      val firstDotIndex = lexemeCandidate.indexOf('.')
      val secondDotIndex = lexemeCandidate.indexOf('.', startIndex = firstDotIndex + 1)
      val errorIndex = index + secondDotIndex
      return ProcessingResult(
        token = null,
        newIndex = errorIndex,
        error = "[line $lineNumber] Error: Unexpected character: .",
      )
    }

    val numericValue = lexemeCandidate.toDoubleOrNull()
    return ProcessingResult(
      token = Token(TokenType.NUMBER, lexemeCandidate, numericValue),
      newIndex = index + lexemeCandidate.length,
      error = null,
    )
  }
}
