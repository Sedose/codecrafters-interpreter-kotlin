package io.codecrafters.tokenizer

import io.codecrafters.model.ProcessingResult // Import for clarity
import io.codecrafters.model.Token
import io.codecrafters.model.TokenizationResult
import io.codecrafters.tokenizer.component.TokenProcessor
import org.koin.core.component.KoinComponent

class Tokenizer(
  private val processors: List<TokenProcessor>,
) : KoinComponent {
  fun tokenize(input: String): TokenizationResult {
    val tokens = mutableListOf<Token>()
    val errors = mutableListOf<String>()
    var lineNumber = 1
    var index = 0

    while (index in input.indices) {
      val currentChar = input[index]
      if (currentChar.isWhitespace()) {
        if (currentChar == '\n') {
          lineNumber++
        }
        index++
        continue
      }

      val processor = processors.firstOrNull { it.canProcess(input, index) }
      if (processor == null) {
        errors.add("[line $lineNumber] Error: Unexpected character: $currentChar")
        index++
      } else {
        val result: ProcessingResult = processor.process(input, index, lineNumber)
        result.token?.let(tokens::add)
        result.error?.let(errors::add)

        index = result.newIndex
      }
    }

    return TokenizationResult(tokens, errors)
  }
}
