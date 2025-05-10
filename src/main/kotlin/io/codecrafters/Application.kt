package io.codecrafters

import io.codecrafters.interpreter.Interpreter
import io.codecrafters.model.CliArgs
import io.codecrafters.model.Command
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.error.NotEnoughCliArgsException
import io.codecrafters.model.error.TokenizationErrorsDetectedException
import io.codecrafters.model.error.UnknownCommandException
import io.codecrafters.parser.AstStringifier
import io.codecrafters.parser.Expr
import io.codecrafters.parser.Parser
import io.codecrafters.tokenizer.Tokenizer
import java.io.File

class Application(
  private val tokenizer: Tokenizer,
  private val astStringifier: AstStringifier,
  private val interpreter: Interpreter,
) {
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
        val result = interpreter.evaluate(expr)
        result.toLoxString().let(::println)
      }
      Command.RUN -> TODO("Run command is not implemented!!!")
    }
  }

  private fun parseCliArgs(args: Array<String>): CliArgs {
    if (args.size < 2) {
      throw NotEnoughCliArgsException()
    }
    val (commandString, filename) = args
    val command =
      Command.parse(commandString)
        ?: run {
          throw UnknownCommandException(commandString)
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
      throw TokenizationErrorsDetectedException()
    }
  }

  private fun parseTokens(
    tokens: List<Token>,
    errors: List<String>,
  ): Expr {
    if (errors.isNotEmpty()) {
      throw TokenizationErrorsDetectedException()
    }
    val tokenList =
      if (tokens.lastOrNull()?.type == TokenType.EOF) {
        tokens
      } else {
        tokens + Token(type = TokenType.EOF, lexeme = "", literal = null, lineNumber = -1)
      }
    return Parser(tokenList).parse()
  }

  private fun Any?.toLoxString(): String =
    when (this) {
      null -> "nil"
      else -> this.toString()
    }
}
