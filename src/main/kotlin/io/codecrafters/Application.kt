package io.codecrafters

import io.codecrafters.interpreter.Interpreter
import io.codecrafters.model.CliArgs
import io.codecrafters.model.Command
import io.codecrafters.model.Expr
import io.codecrafters.model.Stmt
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.error.NotEnoughCliArgsException
import io.codecrafters.model.error.TokenizationErrorsDetectedException
import io.codecrafters.model.error.UnknownCommandException
import io.codecrafters.parser.AstStringifier
import io.codecrafters.parser.Parser
import io.codecrafters.tokenizer.Tokenizer
import java.io.File

class Application(
  private val tokenizer: Tokenizer,
  private val astStringifier: AstStringifier,
  private val interpreter: Interpreter,
  private val output: (String) -> Unit = ::println,
  private val errOutput: (String) -> Unit = System.err::println,
) {
  fun run(commandLineArguments: Array<String>) {
    val cliArgs = parseCliArgs(commandLineArguments)
    val (tokens, errors) =
      File(cliArgs.filename)
        .readText()
        .let(tokenizer::tokenize)

    errors.forEach(errOutput)

    when (cliArgs.command) {
      Command.TOKENIZE -> printTokens(tokens, errors)
      Command.PARSE -> {
        parseTokensAsExpr(tokens, errors)
          .let(astStringifier::stringify)
          .let(output)
      }
      Command.EVALUATE -> {
        parseTokensAsExpr(tokens, errors)
          .let(interpreter::evaluate)
          .toLoxString()
          .let(output)
      }
      Command.RUN ->
        parseTokensAsProgram(tokens, errors)
          .let(interpreter::interpret)
    }
  }

  private fun printTokens(
    tokens: List<Token>,
    errors: List<String>,
  ) {
    tokens.forEach {
      output("${it.type} ${it.lexeme} ${it.literal}")
    }
    output("EOF  null")
    if (errors.isNotEmpty()) {
      throw TokenizationErrorsDetectedException()
    }
  }
}

private fun parseCliArgs(args: Array<String>): CliArgs {
  if (args.size < 2) {
    throw NotEnoughCliArgsException()
  }
  val (commandString, filename) = args
  val command =
    Command.parse(commandString)
      ?: throw UnknownCommandException(commandString)
  return CliArgs(command, filename)
}

private fun parseTokensAsExpr(
  tokens: List<Token>,
  errors: List<String>,
): Expr {
  if (errors.isNotEmpty()) throw TokenizationErrorsDetectedException()
  val tokenList = tokens.withEofGuarantied()
  return Parser(tokenList).parse()
}

private fun parseTokensAsProgram(
  tokens: List<Token>,
  errors: List<String>,
): List<Stmt> {
  if (errors.isNotEmpty()) throw TokenizationErrorsDetectedException()
  val tokenList = tokens.withEofGuarantied()
  return Parser(tokenList).parseProgram()
}

private fun List<Token>.withEofGuarantied(): List<Token> =
  if (this.lastOrNull()?.type == TokenType.EOF) {
    this
  } else {
    this + Token(TokenType.EOF, "", null, -1)
  }

private fun Any?.toLoxString(): String =
  when (this) {
    null -> "nil"
    else -> this.toString()
  }
