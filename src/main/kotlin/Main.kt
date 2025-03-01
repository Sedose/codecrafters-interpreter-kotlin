import TokenType.COMMA
import TokenType.DOT
import TokenType.LEFT_BRACE
import TokenType.LEFT_PAREN
import TokenType.MINUS
import TokenType.PLUS
import TokenType.RIGHT_BRACE
import TokenType.RIGHT_PAREN
import TokenType.SEMICOLON
import TokenType.STAR
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

  if (fileContents.isNotEmpty()) {
    fileContents.toCharArray()
      .joinToString(separator = "\n") { processToken(it) }
      .let(::println)
  }
  println("EOF  null")
}

private val tokenToType =
  mapOf(
    '(' to LEFT_PAREN,
    ')' to RIGHT_PAREN,
    '{' to LEFT_BRACE,
    '}' to RIGHT_BRACE,
    ',' to COMMA,
    '.' to DOT,
    '-' to MINUS,
    '+' to PLUS,
    ';' to SEMICOLON,
    '*' to STAR,
  )

private fun processToken(token: Char): String {
  val tokenType =
    tokenToType[token] ?: throw RuntimeException("Unknown token: $token")
  return "$tokenType $token null"
}
