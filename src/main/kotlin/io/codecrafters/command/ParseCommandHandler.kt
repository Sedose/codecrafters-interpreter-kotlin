package io.codecrafters.command

import io.codecrafters.model.StdoutSink
import io.codecrafters.model.Token
import io.codecrafters.model.error.TokenizationErrorsDetectedException
import io.codecrafters.parser.AstStringifier
import io.codecrafters.parser.Parser
import io.codecrafters.withEofGuaranteed

class ParseCommandHandler(
  private val astStringifier: AstStringifier,
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
    stdout.write(astStringifier.stringify(expression))
  }
}
