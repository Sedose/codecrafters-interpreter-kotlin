package io.codecrafters

import io.codecrafters.model.CliArgs
import io.codecrafters.model.Command
// import io.io.codecrafters.parser.AstPrinter
// import io.io.codecrafters.parser.parseTokens
import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.model.Token
import io.codecrafters.tokenizer.model.TokenType
import java.io.File
import kotlin.system.exitProcess

class Application(
  private val tokenizer: Tokenizer,
//  private val astPrinter: AstPrinter,
) {
  fun run(args: Array<String>) {
    val cliArgs = parseCliArgs(args)
    val (tokens, errors) =
      File(cliArgs.filename)
        .readText()
        .let { tokenizer.tokenize(it) }
    errors.forEach(System.err::println)
    when (cliArgs.command) {
      Command.TOKENIZE -> printTokens(tokens, errors)
      Command.PARSE -> parseFile(tokens, errors)
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

  private fun parseFile(
    tokens: List<Token>,
    errors: List<String>,
  ) {
    if (errors.isNotEmpty()) {
      exitProcess(65)
    }
    val tokenList =
      if (tokens.lastOrNull()?.type == TokenType.EOF) {
        tokens
      } else {
        tokens + Token(type = TokenType.EOF, lexeme = "", literal = null)
      }
//    val (expr, _, hadError) = parseTokens(tokens)
//    if (hadError || expr == null) {
//      System.err.println("Parse error.")
//      exitProcess(65)
//    }
//    println(astPrinter.print(expr))
  }
}
