package io.codecrafters.tokenizer.component.impl

import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.tokenizer.component.TokenProcessor
import org.springframework.stereotype.Component

@Component
class NumberTokenProcessor : TokenProcessor {
  override fun canProcess(
    input: String,
    index: Int,
  ): Boolean = input.getOrNull(index)?.isDigit() ?: false

  override fun process(
    input: String,
    index: Int,
    lineNumber: Int,
  ): ProcessingResult {
    val lexemeCandidate =
      extractLexeme(input.substring(index)) ?: return ProcessingResult(null, index, "[line $lineNumber] Error: Invalid number format.")
    val dotCount = lexemeCandidate.count { it == '.' }
    return if (dotCount > 1) {
      val errorIndex = index + lexemeCandidate.indexOf('.', lexemeCandidate.indexOf('.') + 1)
      ProcessingResult(null, errorIndex, "[line $lineNumber] Error: Unexpected character: .")
    } else {
      ProcessingResult(
        Token(TokenType.NUMBER, lexemeCandidate, lexemeCandidate.toDoubleOrNull(), lineNumber),
        index + lexemeCandidate.length,
        null,
      )
    }
  }

  private fun extractLexeme(input: String): String? = Regex("^\\d[.\\d]*").find(input)?.value
}
