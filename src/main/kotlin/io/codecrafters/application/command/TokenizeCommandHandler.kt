package io.codecrafters.application.command

import io.codecrafters.model.StdoutSink
import io.codecrafters.model.Token
import io.codecrafters.model.error.TokenizationErrorsDetectedException
import org.springframework.stereotype.Component

@Component
class TokenizeCommandHandler(
  private val stdout: StdoutSink,
) : CommandHandler {
  override fun handle(
    tokens: List<Token>,
    errors: List<String>,
  ) {
    for (token in tokens) {
      stdout.write("${token.type} ${token.lexeme} ${token.literal}")
    }
    stdout.write("EOF  null")
    if (errors.isNotEmpty()) {
      throw TokenizationErrorsDetectedException()
    }
  }
}
