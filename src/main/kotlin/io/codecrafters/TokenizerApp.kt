package io.codecrafters

import io.codecrafters.parser.AstPrinter
import io.codecrafters.parser.Parser
import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.model.Token
import io.codecrafters.tokenizer.model.TokenType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.system.exitProcess

class TokenizerApp : KoinComponent {
  private val tokenizer: Tokenizer by inject()

  fun run(args: Array<String>) {
    System.err.println("Logs from your program will appear here!")

    if (args.size < 2) {
      System.err.println("Usage: ./your_program.sh <command> <filename>")
      exitProcess(1)
    }

    val (command, filename) = args
    when (command) {
      "tokenize" -> tokenizeFile(filename)
      "parse" -> parseFile(filename)
      else -> {
        System.err.println("Unknown command: $command")
        exitProcess(1)
      }
    }
  }

  private fun tokenizeFile(filename: String) {
    val fileContents = File(filename).readText()

    val result = tokenizer.tokenize(fileContents)

    result.tokens.forEach { token ->
      println("${token.type} ${token.lexeme} ${token.literal}")
    }

    println("EOF  null")

    if (result.errors.isNotEmpty()) {
      result.errors.forEach(System.err::println)
      exitProcess(65)
    }
  }

  private fun parseFile(filename: String) {
    val fileContents = File(filename).readText()
    val result = tokenizer.tokenize(fileContents)

    if (result.errors.isNotEmpty()) {
      result.errors.forEach(System.err::println)
      exitProcess(65)
    }

    // Insert an EOF token if not present, so the Parser can rely on it.
    val tokens =
      if (result.tokens.lastOrNull()?.type == TokenType.EOF) {
        result.tokens
      } else {
        result.tokens +
          Token(
            type = TokenType.EOF,
            lexeme = "",
            literal = null,
          )
      }

    val parser = Parser(tokens)
    val expr = parser.parse()

    if (parser.hadError()) {
      System.err.println("Parse error occurred.")
      exitProcess(65)
    }

    val output = AstPrinter().print(expr)
    println(output)
  }
}
