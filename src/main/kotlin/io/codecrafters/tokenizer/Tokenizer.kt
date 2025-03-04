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

      currentIndex =
        when {
          char.isWhitespace() -> handleWhitespace(currentIndex).also { if (char == '\n') lineNumber++ }
          char == '/' && nextChar == '/' -> commentSkipper.skipSingleLineComment(input, currentIndex)
          multiCharProcessor.isMultiCharToken(char) && multiCharProcessor.process(char, nextChar) != null ->
            handleMultiCharToken(char, nextChar, tokens, currentIndex)
          singleCharProcessor.canProcess(char) -> handleSingleCharToken(char, tokens, currentIndex)
          char == '"' -> handleString(input, currentIndex, lineNumber, tokens, errors)
          char.isDigit() -> handleNumber(input, currentIndex, lineNumber, tokens, errors)
          char.isLetter() || char == '_' -> handleIdentifier(input, currentIndex, tokens)
          else -> handleUnexpectedChar(char, currentIndex, lineNumber, errors)
        }
    }

    return TokenizationResult(tokens, errors)
  }

  private fun handleWhitespace(index: Int) = index + 1

  private fun handleMultiCharToken(
    char: Char,
    nextChar: Char?,
    tokens: MutableList<Token>,
    index: Int,
  ): Int {
    tokens.add(multiCharProcessor.process(char, nextChar)!!)
    return index + 2
  }

  private fun handleSingleCharToken(
    char: Char,
    tokens: MutableList<Token>,
    index: Int,
  ): Int {
    tokens.add(singleCharProcessor.process(char))
    return index + 1
  }

  private fun handleString(
    input: String,
    index: Int,
    lineNumber: Int,
    tokens: MutableList<Token>,
    errors: MutableList<String>,
  ): Int {
    val (token, newIndex, error) = stringProcessor.processString(input, index, lineNumber)
    token?.let(tokens::add)
    error?.let(errors::add)
    return newIndex
  }

  private fun handleNumber(
    input: String,
    index: Int,
    lineNumber: Int,
    tokens: MutableList<Token>,
    errors: MutableList<String>,
  ): Int {
    val (token, newIndex, error) = numberProcessor.processNumber(input, index, lineNumber)
    token?.let(tokens::add)
    error?.let(errors::add)
    return newIndex
  }

  private fun handleIdentifier(
    input: String,
    index: Int,
    tokens: MutableList<Token>,
  ): Int {
    val (token, newIndex) = identifierProcessor.processIdentifierOrKeyword(input, index)
    tokens.add(token)
    return newIndex
  }

  private fun handleUnexpectedChar(
    char: Char,
    index: Int,
    lineNumber: Int,
    errors: MutableList<String>,
  ): Int {
    errors.add("[line $lineNumber] Error: Unexpected character: $char")
    return index + 1
  }
}
