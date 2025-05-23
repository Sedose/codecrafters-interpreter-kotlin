package io.codecrafters.application

import io.codecrafters.application.command.CommandHandler
import io.codecrafters.model.Command
import io.codecrafters.model.StderrSink
import io.codecrafters.tokenizer.LexError
import io.codecrafters.tokenizer.Tokenizer

class Application(
  private val tokenizer: Tokenizer,
  private val commandHandlers: Map<Command, CommandHandler>,
  private val stderr: StderrSink,
  private val cliArgumentsParser: CliArgumentsParser,
) {
  fun run(commandLineArguments: Array<String>) {
    val cliArgs = cliArgumentsParser.parse(commandLineArguments)
    val (tokens, errors) =
      java.io
        .File(cliArgs.filename)
        .readText()
        .let(tokenizer::tokenize)
    for (lexError in errors) {
      stderr.write(lexError.toString())
    }
    commandHandlers.getValue(cliArgs.command).handle(tokens, errors.map(LexError::toString))
  }
}
