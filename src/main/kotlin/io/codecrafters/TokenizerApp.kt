package io.codecrafters

import io.codecrafters.model.CliArgs
import io.codecrafters.model.Command
import io.codecrafters.parser.AstPrinter
import io.codecrafters.parser.parseTokens
import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.model.Token
import io.codecrafters.tokenizer.model.TokenType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.system.exitProcess

class TokenizerApp : KoinComponent {
  private val tokenizer: Tokenizer by inject()
  private val astPrinter: AstPrinter by inject()

  fun run(args: Array<String>) {
    System.err.println("Logs from your program will appear here!")
    val cli = parseCliArgs(args)
    when (cli.command) {
      Command.TOKENIZE -> tokenizeFile(cli.filename)
      Command.PARSE -> parseFile(cli.filename)
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

  private fun tokenizeFile(filename: String) {
    val (tokens, errors) = tokenizer.tokenize(File(filename).readText())
    tokens.forEach { println("${it.type} ${it.lexeme} ${it.literal}") }
    println("EOF  null")
    if (errors.isNotEmpty()) {
      errors.forEach(System.err::println)
      exitProcess(65)
    }
  }

  private fun parseFile(filename: String) {
    val (tokenList, errors) = tokenizer.tokenize(File(filename).readText())
    if (errors.isNotEmpty()) {
      errors.forEach(System.err::println)
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
