package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.ProcessingResult
import io.codecrafters.tokenizer.component.TokenProcessor
import org.koin.core.component.KoinComponent

class SingleLineCommentSkipper :
  TokenProcessor,
  KoinComponent {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean {
    val nextIndex = index + 1
    return nextIndex <= input.lastIndex &&
      input[index] == '/' &&
      input[nextIndex] == '/'
  }

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult =
    ProcessingResult(
      token = null,
      newIndex =
        input
          .indexOf('\n', index)
          .takeIf { it != -1 }
          ?: input.length,
      error = null,
    )
}
