package io.codecrafters

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly

class TokenizerTest : StringSpec({

  "should tokenize single character symbols" {
    val tokenizer = Tokenizer()
    val input = "(){},.-+;*=/"
    val result = tokenizer.tokenize(input)

    result.tokens.map { it.type } shouldContainExactly listOf(
      TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN,
      TokenType.LEFT_BRACE, TokenType.RIGHT_BRACE,
      TokenType.COMMA, TokenType.DOT,
      TokenType.MINUS, TokenType.PLUS,
      TokenType.SEMICOLON, TokenType.STAR,
      TokenType.EQUAL, TokenType.SLASH
    )
  }

  "should tokenize multi-character operators" {
    val tokenizer = Tokenizer()
    val input = "== != <= >="
    val result = tokenizer.tokenize(input)

    result.tokens.map { it.type } shouldContainExactly listOf(
      TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL,
      TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL
    )
  }

  "should tokenize numbers correctly" {
    val tokenizer = Tokenizer()
    val input = "42 3.14"
    val result = tokenizer.tokenize(input)

    result.tokens shouldContainExactly listOf(
      Token(TokenType.NUMBER, "42", 42.0),
      Token(TokenType.NUMBER, "3.14", 3.14)
    )
  }

  "should tokenize string literals" {
    val tokenizer = Tokenizer()
    val input = "\"hello\" \"world\""
    val result = tokenizer.tokenize(input)

    result.tokens shouldContainExactly listOf(
      Token(TokenType.STRING, "\"hello\"", "hello"),
      Token(TokenType.STRING, "\"world\"", "world")
    )
  }

  "should tokenize identifiers and keywords" {
    val tokenizer = Tokenizer()
    val input = "var foo = 10 if else true false"
    val result = tokenizer.tokenize(input)

    result.tokens.map { it.type } shouldContainExactly listOf(
      TokenType.VAR, TokenType.IDENTIFIER,
      TokenType.EQUAL, TokenType.NUMBER,
      TokenType.IF, TokenType.ELSE,
      TokenType.TRUE, TokenType.FALSE
    )
  }

  "should detect unterminated string" {
    val tokenizer = Tokenizer()
    val input = "\"unterminated"
    val result = tokenizer.tokenize(input)

    result.errors shouldContainExactly listOf("[line 1] Error: Unterminated string.")
  }

  "should ignore comments" {
    val tokenizer = Tokenizer()
    val input = "var x = 42 // this is a comment\nprint(x)"
    val result = tokenizer.tokenize(input)

    result.tokens.map { it.type } shouldContainExactly listOf(
      TokenType.VAR, TokenType.IDENTIFIER,
      TokenType.EQUAL, TokenType.NUMBER,
      TokenType.PRINT, TokenType.LEFT_PAREN,
      TokenType.IDENTIFIER, TokenType.RIGHT_PAREN
    )
  }

  "should report unknown characters" {
    val tokenizer = Tokenizer()
    val input = "#"
    val result = tokenizer.tokenize(input)

    result.errors shouldContainExactly listOf("[line 1] Error: Unexpected character: #")
  }
})
