package io.codecrafters.tokenizer

import io.codecrafters.model.Token
import io.codecrafters.model.TokenizationResult
import io.codecrafters.tokenizer.component.IdentifierProcessor
import io.codecrafters.tokenizer.component.MultiCharTokenProcessor
import io.codecrafters.tokenizer.component.NumberTokenProcessor
import io.codecrafters.tokenizer.component.SingleCharTokenProcessor
import io.codecrafters.tokenizer.component.SingleLineCommentSkipper
import io.codecrafters.tokenizer.component.StringTokenProcessor
import org.koin.core.component.KoinComponent

class Tokenizer(
  private val commentSkipper: SingleLineCommentSkipper,
  private val stringProcessor: StringTokenProcessor,
  private val numberProcessor: NumberTokenProcessor,
  private val identifierProcessor: IdentifierProcessor,
  private val singleCharProcessor: SingleCharTokenProcessor,
  private val multiCharProcessor: MultiCharTokenProcessor,
) : KoinComponent {
  fun tokenize(input: String): TokenizationResult {
    val tokens = mutableListOf<Token>()
    val errors = mutableListOf<String>()
    var lineNumber = 1
    var currentIndex = 0

    while (currentIndex < input.length) {
      val char = input[currentIndex]
      val nextChar = input.getOrNull(currentIndex + 1)

      when {
        char.isWhitespace() -> {
          if (char == '\n') lineNumber++
          currentIndex++
        }

        char == '/' && nextChar == '/' -> {
          currentIndex = commentSkipper.skipSingleLineComment(input, currentIndex)
        }

        multiCharProcessor.isMultiCharToken(char) && multiCharProcessor.process(char, nextChar) != null -> {
          tokens.add(multiCharProcessor.process(char, nextChar)!!)
          currentIndex += 2
        }

        singleCharProcessor.canProcess(char) -> {
          tokens.add(singleCharProcessor.process(char))
          currentIndex++
        }

        char == '"' -> {
          val (token, newIndex, error) = stringProcessor.processString(input, currentIndex, lineNumber)
          token?.let(tokens::add)
          error?.let(errors::add)
          currentIndex = newIndex
        }

        char.isDigit() -> {
          val (token, newIndex, error) = numberProcessor.processNumber(input, currentIndex, lineNumber)
          token?.let(tokens::add)
          error?.let(errors::add)
          currentIndex = newIndex
        }

        char.isLetter() || char == '_' -> {
          val (token, newIndex) = identifierProcessor.processIdentifierOrKeyword(input, currentIndex)
          tokens.add(token)
          currentIndex = newIndex
        }

        else -> {
          errors.add("[line $lineNumber] Error: Unexpected character: $char")
          currentIndex++
        }
      }
    }

    return TokenizationResult(tokens, errors)
  }
}
