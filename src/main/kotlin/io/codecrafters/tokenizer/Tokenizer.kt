package io.codecrafters.tokenizer

import io.codecrafters.model.MultiCharToken
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.TokenizationResult
import io.codecrafters.tokenizer.component.IdentifierProcessor
import io.codecrafters.tokenizer.component.NumberTokenProcessor
import io.codecrafters.tokenizer.component.SingleLineCommentSkipper
import io.codecrafters.tokenizer.component.StringTokenProcessor
import org.koin.core.component.KoinComponent

class Tokenizer : KoinComponent {
  private val commentSkipper = SingleLineCommentSkipper()
  private val stringProcessor = StringTokenProcessor()
  private val numberProcessor = NumberTokenProcessor()
  private val identifierProcessor = IdentifierProcessor()

  fun tokenize(input: String): TokenizationResult {
    val tokens = mutableListOf<Token>()
    val errors = mutableListOf<String>()
    var lineNumber = 1
    var currentIndex = 0

    while (currentIndex < input.length) {
      val char = input[currentIndex]

      when {
        char.isWhitespace() -> {
          if (char == '\n') lineNumber++
          currentIndex++
        }

        char == '/' && input.getOrNull(currentIndex + 1) == '/' -> {
          currentIndex = commentSkipper.skipSingleLineComment(input, currentIndex)
        }

        char in multiCharTokens.keys && input.getOrNull(currentIndex + 1) == multiCharTokens[char]?.secondChar -> {
          val (tokenType, secondChar) = multiCharTokens[char]!!
          tokens.add(Token(tokenType, "$char$secondChar"))
          currentIndex += 2
        }

        char in singleCharTokens -> {
          tokens.add(Token(singleCharTokens[char]!!, char.toString()))
          currentIndex++
        }

        char == '"' -> {
          val (token, newIndex, error) = stringProcessor.processString(input, currentIndex, lineNumber)
          token?.let(tokens::add)
          error?.let(errors::add)
          currentIndex = newIndex
        }

        char.isDigit() -> {
          val (token, newIndex, error) = numberProcessor.processNumber(input, currentIndex, lineNumber)
          token?.let(tokens::add)
          error?.let(errors::add)
          currentIndex = newIndex
        }

        char.isLetter() || char == '_' -> {
          val (token, newIndex) = identifierProcessor.processIdentifierOrKeyword(input, currentIndex)
          tokens.add(token)
          currentIndex = newIndex
        }

        else -> {
          errors.add("[line $lineNumber] Error: Unexpected character: $char")
          currentIndex++
        }
      }
    }

    return TokenizationResult(tokens, errors)
  }

  private val singleCharTokens =
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
      '/' to TokenType.SLASH,
      '=' to TokenType.EQUAL,
      '!' to TokenType.BANG,
      '<' to TokenType.LESS,
      '>' to TokenType.GREATER,
    )

  private val multiCharTokens =
    mapOf(
      '=' to MultiCharToken(TokenType.EQUAL_EQUAL, '='),
      '!' to MultiCharToken(TokenType.BANG_EQUAL, '='),
      '<' to MultiCharToken(TokenType.LESS_EQUAL, '='),
      '>' to MultiCharToken(TokenType.GREATER_EQUAL, '='),
    )
}
