package io.codecrafters

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TokenizerTest {
    @Test
    fun `should tokenize multi-character operators`() {
        val tokenizer = Tokenizer()
        val input = "== != <= >="
        val result = tokenizer.tokenize(input)

        val expectedTokens =
            listOf(
                TokenType.EQUAL_EQUAL,
                TokenType.BANG_EQUAL,
                TokenType.LESS_EQUAL,
                TokenType.GREATER_EQUAL,
            )

        assertEquals(expectedTokens, result.tokens.map { it.type })
    }

    @Test
    fun `should tokenize numbers correctly`() {
        val tokenizer = Tokenizer()
        val input = "42 3.14"
        val result = tokenizer.tokenize(input)

        val expectedTokens =
            listOf(
                Token(TokenType.NUMBER, "42", 42.0),
                Token(TokenType.NUMBER, "3.14", 3.14),
            )

        assertEquals(expectedTokens, result.tokens)
    }

    @Test
    fun `should detect invalid numbers`() {
        val tokenizer = Tokenizer()
        val input = "3.14.159"
        val result = tokenizer.tokenize(input)

        assertEquals(listOf("[line 1] Error: Unexpected character: ."), result.errors)
    }

    @Test
    fun `should tokenize string literals`() {
        val tokenizer = Tokenizer()
        val input = "\"hello\" \"world\""
        val result = tokenizer.tokenize(input)

        val expectedTokens =
            listOf(
                Token(TokenType.STRING, "\"hello\"", "hello"),
                Token(TokenType.STRING, "\"world\"", "world"),
            )

        assertEquals(expectedTokens, result.tokens)
    }

    @Test
    fun `should handle empty string literals`() {
        val tokenizer = Tokenizer()
        val input = "\"\""
        val result = tokenizer.tokenize(input)

        val expectedTokens =
            listOf(
                Token(TokenType.STRING, "\"\"", ""),
            )

        assertEquals(expectedTokens, result.tokens)
    }

    @Test
    fun `should tokenize identifiers and keywords`() {
        val tokenizer = Tokenizer()
        val input = "var foo = 10 if else true false"
        val result = tokenizer.tokenize(input)

        val expectedTokens =
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

        assertEquals(expectedTokens, result.tokens.map { it.type })
    }

    @Test
    fun `should differentiate identifiers and keywords`() {
        val tokenizer = Tokenizer()
        val input = "varx"
        val result = tokenizer.tokenize(input)

        val expectedTokens =
            listOf(
                Token(TokenType.IDENTIFIER, "varx", null),
            )

        assertEquals(expectedTokens, result.tokens)
    }

    @Test
    fun `should detect unterminated string`() {
        val tokenizer = Tokenizer()
        val input = "\"unterminated"
        val result = tokenizer.tokenize(input)

        assertEquals(listOf("[line 1] Error: Unterminated string."), result.errors)
    }

    @Test
    fun `should ignore comments`() {
        val tokenizer = Tokenizer()
        val input = "var x = 42 // this is a comment\nprint(x)"
        val result = tokenizer.tokenize(input)

        val expectedTokens =
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

        assertEquals(expectedTokens, result.tokens.map { it.type })
    }

    @Test
    fun `should handle complex expressions`() {
        val tokenizer = Tokenizer()
        val input = "if(x > 10){print(\"yes\");}"
        val result = tokenizer.tokenize(input)

        val expectedTokens =
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

        assertEquals(expectedTokens, result.tokens.map { it.type })
    }

    @Test
    fun `should handle multi-line input`() {
        val tokenizer = Tokenizer()
        val input = "var x = 42;\nvar y = x + 3;"
        val result = tokenizer.tokenize(input)

        val expectedTokens =
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

        assertEquals(expectedTokens, result.tokens.map { it.type })
    }

    @Test
    fun `should report unknown characters`() {
        val tokenizer = Tokenizer()
        val input = "#"
        val result = tokenizer.tokenize(input)

        assertEquals(listOf("[line 1] Error: Unexpected character: #"), result.errors)
    }
}
