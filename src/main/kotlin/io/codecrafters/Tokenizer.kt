package io.codecrafters

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.TokenizationResult
import org.koin.core.component.KoinComponent

class Tokenizer : KoinComponent {
  fun tokenize(input: String): TokenizationResult {
    val tokens = mutableListOf<Token>()
    val errors = mutableListOf<String>()
    var lineNumber = 1
    var current = 0

    while (current < input.length) {
      val char = input[current]

      when {
        char == ' ' || char == '\t' -> {
          current++
        }

        char == '\n' -> {
          lineNumber++
          current++
        }

        char == '/' && input.getOrNull(current + 1) == '/' -> {
          while (current < input.length && input[current] != '\n') {
            current++
          }
        }

        char == '/' -> {
          tokens.add(Token(TokenType.SLASH, "/"))
          current++
        }

        char == '=' && input.getOrNull(current + 1) == '=' -> {
          tokens.add(Token(TokenType.EQUAL_EQUAL, "=="))
          current += 2
        }

        char == '!' && input.getOrNull(current + 1) == '=' -> {
          tokens.add(Token(TokenType.BANG_EQUAL, "!="))
          current += 2
        }

        char == '!' -> {
          tokens.add(Token(TokenType.BANG, "!"))
          current++
        }

        char == '<' && input.getOrNull(current + 1) == '=' -> {
          tokens.add(Token(TokenType.LESS_EQUAL, "<="))
          current += 2
        }

        char == '<' -> {
          tokens.add(Token(TokenType.LESS, "<"))
          current++
        }

        char == '>' && input.getOrNull(current + 1) == '=' -> {
          tokens.add(Token(TokenType.GREATER_EQUAL, ">="))
          current += 2
        }

        char == '>' -> {
          tokens.add(Token(TokenType.GREATER, ">"))
          current++
        }

        char == '"' -> {
          val start = current + 1
          var end = start
          while (end < input.length && input[end] != '"') {
            if (input[end] == '\n') lineNumber++
            end++
          }
          if (end >= input.length) {
            errors.add("[line $lineNumber] Error: Unterminated string.")
            current = end
          } else {
            val lexeme = input.substring(start - 1, end + 1)
            val literal = input.substring(start, end)
            tokens.add(Token(TokenType.STRING, lexeme, literal))
            current = end + 1
          }
        }

        char.isDigit() -> {
          val start = current
          while (current < input.length && input[current].isDigit()) {
            current++
          }
          if (current < input.length && input[current] == '.') {
            current++
            while (current < input.length && input[current].isDigit()) {
              current++
            }
          }
          val lexeme = input.substring(start, current)
          val literal = lexeme.toDouble()
          tokens.add(Token(TokenType.NUMBER, lexeme, literal))
        }

        char.isLetter() || char == '_' -> {
          val start = current
          while (current < input.length && (input[current].isLetterOrDigit() || input[current] == '_')) {
            current++
          }
          val lexeme = input.substring(start, current)
          tokens.add(Token(TokenType.IDENTIFIER, lexeme, null))
        }

        else -> {
          processToken(char)
            ?.let { tokens.add(it) }
            ?: errors.add("[line $lineNumber] Error: Unexpected character: $char")
          current++
        }
      }
    }

    return TokenizationResult(tokens, errors)
  }

  private fun processToken(token: Char): Token? =
    tokenMap[token]
      ?.let { Token(it, token.toString()) }

  private val tokenMap =
    mapOf(
      '(' to TokenType.LEFT_PAREN,
      ')' to TokenType.RIGHT_PAREN,
      '{' to TokenType.LEFT_BRACE,
      '}' to TokenType.RIGHT_BRACE,
      ',' to TokenType.COMMA,
      '.' to TokenType.DOT,
      '-' to TokenType.MINUS,
      '+' to TokenType.PLUS,
      ';' to TokenType.SEMICOLON,
      '*' to TokenType.STAR,
      '=' to TokenType.EQUAL,
      '!' to TokenType.BANG,
      '<' to TokenType.LESS,
      '>' to TokenType.GREATER
    )
}
