package io.codecrafters.application.command

import io.codecrafters.interpreter.Interpreter
import io.codecrafters.model.StdoutSink
import io.codecrafters.model.Token
import io.codecrafters.model.error.TokenizationErrorsDetectedException
import io.codecrafters.parser.Parser
import io.codecrafters.toLoxString
import io.codecrafters.withEofGuaranteed
import org.springframework.stereotype.Component

@Component
class EvaluateCommandHandler(
  private val interpreter: Interpreter,
  private val stdout: StdoutSink,
) : CommandHandler {
  override fun handle(
    tokens: List<Token>,
    errors: List<String>,
  ) {
    if (errors.isNotEmpty()) {
      throw TokenizationErrorsDetectedException()
    }
    val expression = Parser(tokens.withEofGuaranteed()).parse()
    val result = interpreter.evaluate(expression)
    stdout.write(result.toLoxString())
  }
}
