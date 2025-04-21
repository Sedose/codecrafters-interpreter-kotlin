package io.codecrafters.tokenizer

import io.codecrafters.isAfter
import io.codecrafters.model.Token
import io.codecrafters.model.TokenizationResult
import io.codecrafters.tokenizer.component.TokenProcessor

class Tokenizer(
  private val processors: List<TokenProcessor>,
) {
  fun tokenize(input: String): TokenizationResult {
    tailrec fun process(
      currentIndex: Int,
      currentLine: Int,
      collectedTokens: List<Token>,
      collectedErrors: List<String>,
    ): TokenizationResult {
      if (currentIndex isAfter input.lastIndex) {
        return TokenizationResult(collectedTokens, collectedErrors)
      }
      val currentChar = input[currentIndex]
      return if (currentChar.isWhitespace()) {
        val nextLine = if (currentChar == '\n') currentLine + 1 else currentLine
        process(currentIndex + 1, nextLine, collectedTokens, collectedErrors)
      } else {
        val processor = processors.firstOrNull { it.canProcess(input, currentIndex) }
        if (processor == null) {
          val errorMessage = "[line $currentLine] Error: Unexpected character: $currentChar"
          process(currentIndex + 1, currentLine, collectedTokens, collectedErrors + errorMessage)
        } else {
          val result = processor.process(input, currentIndex, currentLine)
          val updatedTokens =
            result.token
              ?.let { collectedTokens + it }
              ?: collectedTokens
          val updatedErrors =
            result.error
              ?.let { collectedErrors + it }
              ?: collectedErrors
          process(result.newIndex, currentLine, updatedTokens, updatedErrors)
        }
      }
    }
    return process(0, 1, emptyList(), emptyList())
  }
}
