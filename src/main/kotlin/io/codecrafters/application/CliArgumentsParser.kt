package io.codecrafters.application

import io.codecrafters.model.CliArgs
import io.codecrafters.model.Command
import io.codecrafters.model.error.NotEnoughCliArgsException
import io.codecrafters.model.error.UnknownCommandException
import org.springframework.stereotype.Component

@Component
class CliArgumentsParser {
  fun parse(args: Array<String>): CliArgs {
    if (args.size < 2) {
      throw NotEnoughCliArgsException()
    }
    val (commandString, filename) = args
    val command =
      Command.parse(commandString)
        ?: throw UnknownCommandException(commandString)
    return CliArgs(command, filename)
  }
}
