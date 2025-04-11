package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.SINGLE_CHAR_TOKENS
import io.codecrafters.model.Token
import io.codecrafters.tokenizer.component.TokenProcessor
import org.koin.core.component.KoinComponent

class SingleCharTokenProcessor :
  TokenProcessor,
  KoinComponent {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = index in input.indices && SINGLE_CHAR_TOKENS.containsKey(input[index])

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val type = SINGLE_CHAR_TOKENS[input[index]]!!
    val token = Token(type, input[index].toString(), null, lineNumber) // Added line number
    return ProcessingResult(
      token = token,
      newIndex = index + 1,
      error = null,
    )
  }
}
