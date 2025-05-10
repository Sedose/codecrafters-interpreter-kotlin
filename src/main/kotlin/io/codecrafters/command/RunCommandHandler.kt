package io.codecrafters.command

import io.codecrafters.interpreter.Interpreter
import io.codecrafters.model.Stmt
import io.codecrafters.model.Token
import io.codecrafters.model.error.TokenizationErrorsDetectedException
import io.codecrafters.parser.Parser
import io.codecrafters.withEofGuaranteed

class RunCommandHandler(
  private val interpreter: Interpreter,
) : CommandHandler {
  override fun handle(
    tokens: List<Token>,
    errors: List<String>,
  ) {
    if (errors.isNotEmpty()) {
      throw TokenizationErrorsDetectedException()
    }
    val statements: List<Stmt> = Parser(tokens.withEofGuaranteed()).parseProgram()
    interpreter.interpret(statements)
  }
}
