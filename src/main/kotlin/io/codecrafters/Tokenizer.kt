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
