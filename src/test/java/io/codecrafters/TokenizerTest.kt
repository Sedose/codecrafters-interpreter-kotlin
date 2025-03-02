package io.codecrafters

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly

class TokenizerTest :
    StringSpec({

        "should tokenize multi-character operators" {
            val tokenizer = Tokenizer()
            val input = "== != <= >="
            val result = tokenizer.tokenize(input)

            result.tokens.map { it.type } shouldContainExactly
                listOf(
                    TokenType.EQUAL_EQUAL,
                    TokenType.BANG_EQUAL,
                    TokenType.LESS_EQUAL,
                    TokenType.GREATER_EQUAL,
                )
        }

        "should tokenize numbers correctly" {
            val tokenizer = Tokenizer()
            val input = "42 3.14"
            val result = tokenizer.tokenize(input)

            result.tokens shouldContainExactly
                listOf(
                    Token(TokenType.NUMBER, "42", 42.0),
                    Token(TokenType.NUMBER, "3.14", 3.14),
                )
        }

        "should detect invalid numbers" {
            val tokenizer = Tokenizer()
            val input = "3.14.159"
            val result = tokenizer.tokenize(input)

            result.errors.shouldContainExactly(
                "[line 1] Error: Unexpected character: .",
            )
        }

        "should tokenize string literals" {
            val tokenizer = Tokenizer()
            val input = "\"hello\" \"world\""
            val result = tokenizer.tokenize(input)

            result.tokens shouldContainExactly
                listOf(
                    Token(TokenType.STRING, "\"hello\"", "hello"),
                    Token(TokenType.STRING, "\"world\"", "world"),
                )
        }

        "should handle empty string literals" {
            val tokenizer = Tokenizer()
            val input = "\"\""
            val result = tokenizer.tokenize(input)

            result.tokens shouldContainExactly
                listOf(
                    Token(TokenType.STRING, "\"\"", ""),
                )
        }

        "should tokenize identifiers and keywords" {
            val tokenizer = Tokenizer()
            val input = "var foo = 10 if else true false"
            val result = tokenizer.tokenize(input)

            result.tokens.map { it.type } shouldContainExactly
                listOf(
                    TokenType.VAR,
                    TokenType.IDENTIFIER,
                    TokenType.EQUAL,
                    TokenType.NUMBER,
                    TokenType.IF,
                    TokenType.ELSE,
                    TokenType.TRUE,
                    TokenType.FALSE,
                )
        }

        "should differentiate identifiers and keywords" {
            val tokenizer = Tokenizer()
            val input = "varx"
            val result = tokenizer.tokenize(input)

            result.tokens shouldContainExactly
                listOf(
                    Token(TokenType.IDENTIFIER, "varx", null),
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

            result.tokens.map { it.type } shouldContainExactly
                listOf(
                    TokenType.VAR,
                    TokenType.IDENTIFIER,
                    TokenType.EQUAL,
                    TokenType.NUMBER,
                    TokenType.PRINT,
                    TokenType.LEFT_PAREN,
                    TokenType.IDENTIFIER,
                    TokenType.RIGHT_PAREN,
                )
        }

        "should handle complex expressions" {
            val tokenizer = Tokenizer()
            val input = "if(x > 10){print(\"yes\");}"
            val result = tokenizer.tokenize(input)

            result.tokens.map { it.type } shouldContainExactly
                listOf(
                    TokenType.IF,
                    TokenType.LEFT_PAREN,
                    TokenType.IDENTIFIER,
                    TokenType.GREATER,
                    TokenType.NUMBER,
                    TokenType.RIGHT_PAREN,
                    TokenType.LEFT_BRACE,
                    TokenType.PRINT,
                    TokenType.LEFT_PAREN,
                    TokenType.STRING,
                    TokenType.RIGHT_PAREN,
                    TokenType.SEMICOLON,
                    TokenType.RIGHT_BRACE,
                )
        }

        "should handle multi-line input" {
            val tokenizer = Tokenizer()
            val input = "var x = 42;\nvar y = x + 3;"
            val result = tokenizer.tokenize(input)

            result.tokens.map { it.type } shouldContainExactly
                listOf(
                    TokenType.VAR,
                    TokenType.IDENTIFIER,
                    TokenType.EQUAL,
                    TokenType.NUMBER,
                    TokenType.SEMICOLON,
                    TokenType.VAR,
                    TokenType.IDENTIFIER,
                    TokenType.EQUAL,
                    TokenType.IDENTIFIER,
                    TokenType.PLUS,
                    TokenType.NUMBER,
                    TokenType.SEMICOLON,
                )
        }

        "should report unknown characters" {
            val tokenizer = Tokenizer()
            val input = "#"
            val result = tokenizer.tokenize(input)

            result.errors shouldContainExactly listOf("[line 1] Error: Unexpected character: #")
        }
    })
