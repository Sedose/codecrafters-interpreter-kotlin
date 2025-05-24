package io.codecrafters.tokenizer.component

import io.codecrafters.model.ProcessingResult

interface TokenProcessor {
  fun canProcess(
    input: String,
    index: Int,
  ): Boolean

  fun process(
    input: String,
    index: Int,
    lineNumber: Int = -1,
  ): ProcessingResult
}
