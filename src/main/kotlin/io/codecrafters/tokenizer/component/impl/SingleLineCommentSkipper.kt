package io.codecrafters.tokenizer.component.impl

import io.codecrafters.tokenizer.component.TokenProcessor
import io.codecrafters.tokenizer.model.ProcessingResult
import org.koin.core.component.KoinComponent

class SingleLineCommentSkipper :
  TokenProcessor,
  KoinComponent {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean {
    val nextIndex = index + 1
    return index in input.indices &&
      nextIndex in input.indices &&
      input[index] == '/' &&
      input[nextIndex] == '/'
  }

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val newLineIndex = input.indexOf('\n', startIndex = index)
    return ProcessingResult(
      token = null,
      newIndex = if (newLineIndex != -1) newLineIndex else input.length,
      error = null,
    )
  }
}
