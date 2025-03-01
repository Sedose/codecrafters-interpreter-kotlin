import TokenType.*
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  System.err.println("Logs from your program will appear here!")

  if (args.size < 2) {
    System.err.println("Usage: ./your_program.sh tokenize <filename>")
    exitProcess(1)
  }

  val (command, filename) = args

  if (command != "tokenize") {
    System.err.println("Unknown command: $command")
    exitProcess(1)
  }

  val fileContents = File(filename).readText()

  val tokenToType =
    mapOf(
      '(' to LEFT_PAREN,
      ')' to RIGHT_PAREN,
    )

  if (fileContents.isNotEmpty()) {
    fileContents.toCharArray()
      .joinToString(separator = "\n") { token ->
        val tokenType = tokenToType[token] ?: throw RuntimeException("Unknown token: $token")
        "$tokenType $token null"
      }.let(::println)
  }
  println("EOF  null")
}
