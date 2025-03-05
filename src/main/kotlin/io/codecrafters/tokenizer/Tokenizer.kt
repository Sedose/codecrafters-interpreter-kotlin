package io.codecrafters.tokenizer

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

    while (index < input.length) {
      val char = input[index]
      if (char.isWhitespace()) {
        if (char == '\n') lineNumber++
        index++
      } else {
        val processor = processors.firstOrNull { it.canProcess(input, index) }
        if (processor == null) {
          errors.add("[line $lineNumber] Error: Unexpected character: $char")
          index++
        } else {
          val (token, newIndex, error) = processor.process(input, index, lineNumber)
          token?.let(tokens::add)
          error?.let(errors::add)
          index = newIndex
        }
      }
    }

    return TokenizationResult(tokens, errors)
  }
}
