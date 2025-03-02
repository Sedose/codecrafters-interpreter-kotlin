package io.codecrafters

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TokenizerTest {
    private val tokenizer = Tokenizer()

    @ParameterizedTest
    @MethodSource("provideMultiCharOperatorTestCases")
    fun `should tokenize multi-character operators`(
        input: String,
        expectedTokenTypes: List<TokenType>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedTokenTypes, result.tokens.map { it.type })
    }

    @ParameterizedTest
    @MethodSource("provideIdentifierAndKeywordTestCases")
    fun `should tokenize identifiers and keywords`(
        input: String,
        expectedTokenTypes: List<TokenType>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedTokenTypes, result.tokens.map { it.type })
    }

    @ParameterizedTest
    @MethodSource("provideCommentTestCases")
    fun `should ignore comments`(
        input: String,
        expectedTokenTypes: List<TokenType>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedTokenTypes, result.tokens.map { it.type })
    }

    @ParameterizedTest
    @MethodSource("provideComplexExpressionTestCases")
    fun `should handle complex expressions`(
        input: String,
        expectedTokenTypes: List<TokenType>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedTokenTypes, result.tokens.map { it.type })
    }

    @ParameterizedTest
    @MethodSource("provideMultiLineInputTestCases")
    fun `should handle multi-line input`(
        input: String,
        expectedTokenTypes: List<TokenType>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedTokenTypes, result.tokens.map { it.type })
    }

    @ParameterizedTest
    @MethodSource("provideParenthesesTestCases")
    fun `should tokenize parentheses correctly`(
        input: String,
        expectedTokenTypes: List<TokenType>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedTokenTypes, result.tokens.map { it.type })
    }

    @ParameterizedTest
    @MethodSource("provideNumberTokenizationTestCases")
    fun `should tokenize numbers correctly`(
        input: String,
        expectedTokens: List<Token>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedTokens, result.tokens)
    }

    @ParameterizedTest
    @MethodSource("provideInvalidNumberTestCases")
    fun `should detect invalid numbers`(
        input: String,
        expectedErrors: List<String>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedErrors, result.errors)
    }



    @ParameterizedTest
    @MethodSource("provideIdentifierVsKeywordTestCases")
    fun `should differentiate identifiers and keywords`(
        input: String,
        expectedTokens: List<Token>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedTokens, result.tokens)
    }

    @ParameterizedTest
    @MethodSource("provideUnterminatedStringTestCases")
    fun `should detect unterminated string`(
        input: String,
        expectedErrors: List<String>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedErrors, result.errors)
    }

    @ParameterizedTest
    @MethodSource("provideUnknownCharacterTestCases")
    fun `should report unknown characters`(
        input: String,
        expectedErrors: List<String>,
    ) {
        val result = tokenizer.tokenize(input)
        assertEquals(expectedErrors, result.errors)
    }



    companion object {
        @JvmStatic
        fun provideMultiCharOperatorTestCases() =
            listOf(
                Arguments.of("==", listOf(TokenType.EQUAL_EQUAL)),
                Arguments.of("!=", listOf(TokenType.BANG_EQUAL)),
                Arguments.of("<=", listOf(TokenType.LESS_EQUAL)),
                Arguments.of(">=", listOf(TokenType.GREATER_EQUAL)),
                Arguments.of(
                    "== != <= >=",
                    listOf(
                        TokenType.EQUAL_EQUAL,
                        TokenType.BANG_EQUAL,
                        TokenType.LESS_EQUAL,
                        TokenType.GREATER_EQUAL,
                    ),
                ),
            )

        @JvmStatic
        fun provideNumberTokenizationTestCases() =
            listOf(
                Arguments.of("42", listOf(Token(TokenType.NUMBER, "42", 42.0))),
                Arguments.of("3.14", listOf(Token(TokenType.NUMBER, "3.14", 3.14))),
                Arguments.of(
                    "42 3.14",
                    listOf(
                        Token(TokenType.NUMBER, "42", 42.0),
                        Token(TokenType.NUMBER, "3.14", 3.14),
                    ),
                ),
                Arguments.of("\"hello\"", listOf(Token(TokenType.STRING, "\"hello\"", "hello"))),
                Arguments.of("\"world\"", listOf(Token(TokenType.STRING, "\"world\"", "world"))),
                Arguments.of("\"\"", listOf(Token(TokenType.STRING, "\"\"", ""))),
                Arguments.of(
                    "\"hello\" \"world\"",
                    listOf(
                        Token(TokenType.STRING, "\"hello\"", "hello"),
                        Token(TokenType.STRING, "\"world\"", "world"),
                    ),
                ),
            )

        @JvmStatic
        fun provideInvalidNumberTestCases() =
            listOf(
                Arguments.of("3.14.159", listOf("[line 1] Error: Unexpected character: .")),
            )

        @JvmStatic
        fun provideIdentifierAndKeywordTestCases() =
            listOf(
                Arguments.of("var", listOf(TokenType.VAR)),
                Arguments.of("if", listOf(TokenType.IF)),
                Arguments.of("else", listOf(TokenType.ELSE)),
                Arguments.of("true", listOf(TokenType.TRUE)),
                Arguments.of("false", listOf(TokenType.FALSE)),
                Arguments.of("foo", listOf(TokenType.IDENTIFIER)),
                Arguments.of(
                    "var foo = 10 if else true false",
                    listOf(
                        TokenType.VAR,
                        TokenType.IDENTIFIER,
                        TokenType.EQUAL,
                        TokenType.NUMBER,
                        TokenType.IF,
                        TokenType.ELSE,
                        TokenType.TRUE,
                        TokenType.FALSE,
                    ),
                ),
            )

        @JvmStatic
        fun provideIdentifierVsKeywordTestCases() =
            listOf(
                Arguments.of("varx", listOf(Token(TokenType.IDENTIFIER, "varx", null))),
                Arguments.of("ifelse", listOf(Token(TokenType.IDENTIFIER, "ifelse", null))),
                Arguments.of("_var", listOf(Token(TokenType.IDENTIFIER, "_var", null))),
            )

        @JvmStatic
        fun provideUnterminatedStringTestCases() =
            listOf(
                Arguments.of("\"unterminated", listOf("[line 1] Error: Unterminated string.")),
            )

        @JvmStatic
        fun provideCommentTestCases() =
            listOf(
                Arguments.of("// this is a comment", listOf<TokenType>()),
                Arguments.of(
                    "var x = 42 // this is a comment",
                    listOf(
                        TokenType.VAR,
                        TokenType.IDENTIFIER,
                        TokenType.EQUAL,
                        TokenType.NUMBER,
                    ),
                ),
                Arguments.of(
                    "var x = 42 // this is a comment\nprint(x)",
                    listOf(
                        TokenType.VAR,
                        TokenType.IDENTIFIER,
                        TokenType.EQUAL,
                        TokenType.NUMBER,
                        TokenType.PRINT,
                        TokenType.LEFT_PAREN,
                        TokenType.IDENTIFIER,
                        TokenType.RIGHT_PAREN,
                    ),
                ),
            )

        @JvmStatic
        fun provideComplexExpressionTestCases() =
            listOf(
                Arguments.of(
                    "if(x > 10){print(\"yes\");}",
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
                    ),
                ),
                Arguments.of(
                    "while(true){var x = x + 1;}",
                    listOf(
                        TokenType.WHILE,
                        TokenType.LEFT_PAREN,
                        TokenType.TRUE,
                        TokenType.RIGHT_PAREN,
                        TokenType.LEFT_BRACE,
                        TokenType.VAR,
                        TokenType.IDENTIFIER,
                        TokenType.EQUAL,
                        TokenType.IDENTIFIER,
                        TokenType.PLUS,
                        TokenType.NUMBER,
                        TokenType.SEMICOLON,
                        TokenType.RIGHT_BRACE,
                    ),
                ),
            )

        @JvmStatic
        fun provideMultiLineInputTestCases() =
            listOf(
                Arguments.of(
                    "var x = 42;\nvar y = x + 3;",
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
                    ),
                ),
                Arguments.of(
                    "if(x)\n{\n  print(x);\n}",
                    listOf(
                        TokenType.IF,
                        TokenType.LEFT_PAREN,
                        TokenType.IDENTIFIER,
                        TokenType.RIGHT_PAREN,
                        TokenType.LEFT_BRACE,
                        TokenType.PRINT,
                        TokenType.LEFT_PAREN,
                        TokenType.IDENTIFIER,
                        TokenType.RIGHT_PAREN,
                        TokenType.SEMICOLON,
                        TokenType.RIGHT_BRACE,
                    ),
                ),
            )

        @JvmStatic
        fun provideUnknownCharacterTestCases() =
            listOf(
                Arguments.of("#", listOf("[line 1] Error: Unexpected character: #")),
                Arguments.of("@", listOf("[line 1] Error: Unexpected character: @")),
                Arguments.of("$", listOf("[line 1] Error: Unexpected character: $")),
            )

        @JvmStatic
        fun provideParenthesesTestCases() =
            listOf(
                Arguments.of("(", listOf(TokenType.LEFT_PAREN)),
                Arguments.of(")", listOf(TokenType.RIGHT_PAREN)),
                Arguments.of("()", listOf(TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN)),
                Arguments.of("(()", listOf(TokenType.LEFT_PAREN, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN)),
                Arguments.of("())", listOf(TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN, TokenType.RIGHT_PAREN)),
            )
    }
}
