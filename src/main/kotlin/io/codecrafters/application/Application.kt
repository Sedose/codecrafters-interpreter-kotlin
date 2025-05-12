package io.codecrafters.application

import io.codecrafters.application.command.CommandHandler
import io.codecrafters.model.CliArgs
import io.codecrafters.model.Command
import io.codecrafters.model.StderrSink
import io.codecrafters.model.error.NotEnoughCliArgsException
import io.codecrafters.model.error.UnknownCommandException
import io.codecrafters.tokenizer.Tokenizer
import java.io.File

class Application(
  private val tokenizer: Tokenizer,
  private val commandHandlers: Map<Command, CommandHandler>,
  private val stderr: StderrSink,
) {
  fun run(commandLineArguments: Array<String>) {
    val cliArgs = parseCliArguments(commandLineArguments)
    val (tokens, errors) =
      File(cliArgs.filename)
        .readText()
        .let(tokenizer::tokenize)

    for (error in errors) {
      stderr.write(error)
    }

    commandHandlers
      .getValue(cliArgs.command)
      .handle(tokens, errors)
  }

  private fun parseCliArguments(args: Array<String>): CliArgs {
    if (args.size < 2) {
      throw NotEnoughCliArgsException()
    }
    val (commandString, filename) = args
    val command =
      Command.Companion.parse(commandString)
        ?: throw UnknownCommandException(commandString)
    return CliArgs(command, filename)
  }
}
