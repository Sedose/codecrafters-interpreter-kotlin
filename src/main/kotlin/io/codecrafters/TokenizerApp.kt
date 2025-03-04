package io.codecrafters

import io.codecrafters.tokenizer.Tokenizer
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.io.File
import kotlin.system.exitProcess

@Component
class TokenizerApp(
  private val tokenizer: Tokenizer,
) : ApplicationRunner {
  override fun run(args: ApplicationArguments) {
    System.err.println("Logs from your program will appear here!")

    val commandLineArgs = args.sourceArgs
    if (commandLineArgs.size < 2) {
      System.err.println("Usage: ./your_program.sh tokenize <filename>")
      exitProcess(1)
    }

    val (command, filename) = commandLineArgs

    if (command != "tokenize") {
      System.err.println("Unknown command: $command")
      exitProcess(1)
    }

    val fileContents = File(filename).readText()
    val result = tokenizer.tokenize(fileContents)

    result.tokens.forEach { println("${it.type} ${it.lexeme} ${it.literal}") }
    println("EOF null")

    if (result.errors.isNotEmpty()) {
      result.errors.forEach(System.err::println)
      exitProcess(65)
    }
  }
}
