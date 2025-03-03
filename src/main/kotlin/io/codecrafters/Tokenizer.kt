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
                char.isWhitespace() -> {
                    if (char == '\n') lineNumber++
                    current++
                }

                char == '/' && input.getOrNull(current + 1) == '/' -> {
                    current = skipSingleLineComment(input, current)
                }

                char in multiCharTokens.keys && input.getOrNull(current + 1) == multiCharTokens[char]?.second -> {
                    val (tokenType, secondChar) = multiCharTokens[char]!!
                    tokens.add(Token(tokenType, "$char$secondChar"))
                    current += 2
                }

                char in singleCharTokens -> {
                    tokens.add(Token(singleCharTokens[char]!!, char.toString()))
                    current++
                }

                char == '"' -> {
                    val (token, newIndex, error) = processString(input, current, lineNumber)
                    token?.let(tokens::add)
                    error?.let(errors::add)
                    current = newIndex
                }

                char.isDigit() -> {
                    val (token, newIndex, error) = processNumber(input, current, lineNumber)
                    token?.let(tokens::add)
                    error?.let(errors::add)
                    current = newIndex
                }

                char.isLetter() || char == '_' -> {
                    val (token, newIndex) = processIdentifierOrKeyword(input, current)
                    tokens.add(token)
                    current = newIndex
                }

                else -> {
                    errors.add("[line $lineNumber] Error: Unexpected character: $char")
                    current++
                }
            }
        }

        return TokenizationResult(tokens, errors)
    }

    private fun skipSingleLineComment(
        input: String,
        current: Int,
    ): Int =
        input
            .indexOf('\n', current)
            .takeIf { it != -1 }
            ?: input.length

    private fun processString(
        input: String,
        startIndex: Int,
        lineNumber: Int,
    ): StringProcessing {
        val endIndex =
            (startIndex + 1 until input.length)
                .find { input[it] == '"' || input[it] == '\n' }
                ?: input.length
        return when {
            endIndex >= input.length || input[endIndex] == '\n' ->
                StringProcessing(null, endIndex, "[line $lineNumber] Error: Unterminated string.")

            else ->
                StringProcessing(
                    Token(
                        TokenType.STRING,
                        input.substring(startIndex, endIndex + 1),
                        input.substring(startIndex + 1, endIndex),
                    ),
                    endIndex + 1,
                    null,
                )
        }
    }

    private fun processNumber(
        input: String,
        startIndex: Int,
        lineNumber: Int,
    ): StringProcessing {
        var currentIndex = startIndex

        while (
            currentIndex < input.length &&
            (input[currentIndex].isDigit() || input[currentIndex] == '.')
        ) {
            currentIndex++
        }

        val lexeme = input.substring(startIndex, currentIndex)

        if (lexeme.count { it == '.' } > 1) {
            return StringProcessing(
                token = null,
                newIndex = currentIndex,
                error = "[line $lineNumber] Error: Unexpected character: .",
            )
        }

        val numericValue = lexeme.toDoubleOrNull()
        return StringProcessing(
            token = Token(TokenType.NUMBER, lexeme, numericValue),
            newIndex = currentIndex,
            error = null,
        )
    }

    private fun processIdentifierOrKeyword(
        input: String,
        startIndex: Int,
    ): IdentifierProcessingResult {
        var index = startIndex
        while (index < input.length && (input[index].isLetterOrDigit() || input[index] == '_')) {
            index++
        }

        val lexeme = input.substring(startIndex, index)
        val tokenType = reservedWords[lexeme] ?: TokenType.IDENTIFIER
        return IdentifierProcessingResult(Token(tokenType, lexeme), index)
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
            '=' to Pair(TokenType.EQUAL_EQUAL, '='),
            '!' to Pair(TokenType.BANG_EQUAL, '='),
            '<' to Pair(TokenType.LESS_EQUAL, '='),
            '>' to Pair(TokenType.GREATER_EQUAL, '='),
        )

    private val reservedWords =
        mapOf(
            "and" to TokenType.AND,
            "class" to TokenType.CLASS,
            "else" to TokenType.ELSE,
            "false" to TokenType.FALSE,
            "for" to TokenType.FOR,
            "fun" to TokenType.FUN,
            "if" to TokenType.IF,
            "nil" to TokenType.NIL,
            "or" to TokenType.OR,
            "print" to TokenType.PRINT,
            "return" to TokenType.RETURN,
            "super" to TokenType.SUPER,
            "this" to TokenType.THIS,
            "true" to TokenType.TRUE,
            "var" to TokenType.VAR,
            "while" to TokenType.WHILE,
        )
}

data class StringProcessing(
    val token: Token?,
    val newIndex: Int,
    val error: String?,
)

data class IdentifierProcessingResult(
    val token: Token,
    val newIndex: Int,
)
