package io.codecrafters.tokenizer

import io.codecrafters.model.Token
import io.codecrafters.model.TokenizationResult
import io.codecrafters.tokenizer.component.TokenProcessor

class Tokenizer(
  private val tokenProcessors: List<TokenProcessor>,
) {
  fun tokenize(source: String): TokenizationResult {
    var currentIndex = 0
    var currentLine = 1
    val producedTokens = mutableListOf<Token>()
    val detectedErrors = mutableListOf<LexError>()
    val lastIndex = source.lastIndex
    while (currentIndex <= lastIndex) {
      val currentChar = source[currentIndex]
      if (currentChar == '\n') {
        currentLine += 1
        currentIndex += 1
        continue
      }
      if (currentChar.isWhitespace()) {
        currentIndex += 1
        continue
      }
      var processed = false
      for (processor in tokenProcessors) {
        when (val result = processor.process(source, currentIndex, currentLine)) {
          is TokenProcessorResult.Produced -> {
            producedTokens.add(result.token)
            currentIndex = result.newIndex
            processed = true
          }
          is TokenProcessorResult.Skipped -> {
            currentIndex = result.newIndex
            processed = true
          }
          is TokenProcessorResult.Error -> {
            detectedErrors.add(result.error)
            currentIndex = result.newIndex
            processed = true
          }
        }
        if (processed) break
      }
      if (!processed) {
        detectedErrors.add(LexError(currentLine, "Error: Unexpected character: $currentChar"))
        currentIndex += 1
      }
    }
    return TokenizationResult(producedTokens, detectedErrors)
  }
}
