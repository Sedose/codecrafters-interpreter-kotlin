package io.codecrafters

import io.codecrafters.model.CliArgs
import io.codecrafters.model.Command
import io.codecrafters.parser.AstPrinter
import io.codecrafters.parser.parseTokens
import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.model.Token
import io.codecrafters.tokenizer.model.TokenType
import java.io.File
import kotlin.system.exitProcess

class Application(
  private val tokenizer: Tokenizer,
  private val astPrinter: AstPrinter,
) {
  fun run(args: Array<String>) {
    val cliArgs = parseCliArgs(args)
    val (tokenList, errors) =
      File(cliArgs.filename)
        .readText()
        .let { tokenizer.tokenize(it) }
    errors.forEach(System.err::println)
    when (cliArgs.command) {
      Command.TOKENIZE -> printTokens(tokenList, errors)
      Command.PARSE -> parseFile(tokenList, errors)
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
    tokenList: List<Token>,
    errors: List<String>,
  ) {
    tokenList.forEach {
      println("${it.type} ${it.lexeme} ${it.literal}")
    }
    println("EOF  null")
    if (errors.isNotEmpty()) {
      exitProcess(65)
    }
  }

  private fun parseFile(
    tokenList: List<Token>,
    errors: List<String>,
  ) {
    if (errors.isNotEmpty()) {
      exitProcess(65)
    }
    val tokens =
      if (tokenList.lastOrNull()?.type == TokenType.EOF) {
        tokenList
      } else {
        tokenList + Token(type = TokenType.EOF, lexeme = "", literal = null)
      }
    val (expr, _, hadError) = parseTokens(tokens)
    if (hadError || expr == null) {
      System.err.println("Parse error.")
      exitProcess(65)
    }
    println(astPrinter.print(expr))
  }
}
