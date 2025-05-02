package io.codecrafters

import arrow.core.Either
import arrow.core.raise.ExperimentalTraceApi
import arrow.core.raise.either
import arrow.core.raise.traced
import io.codecrafters.interpreter.Interpreter
import io.codecrafters.model.CliArgs
import io.codecrafters.model.Command
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.parser.AstStringifier
import io.codecrafters.parser.Expr
import io.codecrafters.parser.Parser
import io.codecrafters.tokenizer.Tokenizer
import java.io.File
import kotlin.system.exitProcess

class Application(
  private val tokenizer: Tokenizer,
  private val astStringifier: AstStringifier,
  private val interpreter: Interpreter,
) {
  @OptIn(ExperimentalTraceApi::class)
  fun run(commandLineArguments: Array<String>) {
    val cliArgs = parseCliArgs(commandLineArguments)
    val (tokens, errors) =
      File(cliArgs.filename)
        .readText()
        .let { tokenizer.tokenize(it) }

    errors.forEach(System.err::println)

    when (cliArgs.command) {
      Command.TOKENIZE -> printTokens(tokens, errors)
      Command.PARSE -> {
        parseTokens(tokens, errors)
          .let { astStringifier.stringify(it) }
          .let { println(it) }
      }
      Command.EVALUATE -> {
        val expr = parseTokens(tokens, errors)
        val either =
          either {
            traced(
              block = {
                interpreter.evaluate(expr)
              },
              trace = { trace, error ->
                println("Error: $error")
                println("Trace: $trace")
              },
            )
          }
        when (either) {
          is Either.Left -> {
            System.err.println(either.value.message)
            System.err.println("[line ${either.value.lineNumber}]")
            exitProcess(70)
          }
          is Either.Right -> either.value.toLoxString().let { println(it) }
        }
      }
    }
  }

  private fun parseCliArgs(args: Array<String>): CliArgs {
    if (args.size < 2) {
      System.err.println("Usage: ./your_program.sh <command> <filename>")
      exitProcess(1)
    }
    val (commandString, filename) = args
    val command =
      Command.parse(commandString)
        ?: run {
          System.err.println("Unknown command: $commandString")
          exitProcess(1)
        }
    return CliArgs(command, filename)
  }

  private fun printTokens(
    tokens: List<Token>,
    errors: List<String>,
  ) {
    tokens.forEach {
      println("${it.type} ${it.lexeme} ${it.literal}")
    }
    println("EOF  null")
    if (errors.isNotEmpty()) {
      exitProcess(65)
    }
  }

  private fun parseTokens(
    tokens: List<Token>,
    errors: List<String>,
  ): Expr {
    if (errors.isNotEmpty()) {
      exitProcess(65)
    }
    val tokenList =
      if (tokens.lastOrNull()?.type == TokenType.EOF) {
        tokens
      } else {
        tokens + Token(type = TokenType.EOF, lexeme = "", literal = null, lineNumber = -1)
      }
    return when (val result = either { Parser(tokenList, this).parse() }) {
      is Either.Left -> {
        val error = result.value
        System.err.println("[line ${error.token.lineNumber}] Error at '${error.token.lexeme}': ${error.message}")
        exitProcess(65)
      }
      is Either.Right -> result.value
    }
  }

  private fun Any?.toLoxString(): String =
    when (this) {
      null -> "nil"
      else -> this.toString()
    }
}
