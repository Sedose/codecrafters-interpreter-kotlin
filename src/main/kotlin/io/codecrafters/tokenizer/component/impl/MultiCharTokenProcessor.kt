package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.MultiCharToken
import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor
import org.koin.core.component.KoinComponent

class MultiCharTokenProcessor :
  TokenProcessor,
  KoinComponent {
  private val tokens =
    mapOf(
      '=' to MultiCharToken(TokenType.EQUAL_EQUAL, '='),
      '!' to MultiCharToken(TokenType.BANG_EQUAL, '='),
      '<' to MultiCharToken(TokenType.LESS_EQUAL, '='),
      '>' to MultiCharToken(TokenType.GREATER_EQUAL, '='),
    )

  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean =
    index + 1 < input.length &&
      tokens.containsKey(input[index]) &&
      tokens[input[index]]?.secondChar == input[index + 1]

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val char = input[index]
    val nextChar = input[index + 1]
    val tokenType = tokens[char]!!.tokenType
    val token = Token(tokenType, "$char$nextChar")
    return ProcessingResult(token, index + 2, null)
  }
}
