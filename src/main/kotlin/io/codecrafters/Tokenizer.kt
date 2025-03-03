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
        var currentIndex = 0

        while (currentIndex < input.length) {
            val char = input[currentIndex]

            when {
                char.isWhitespace() -> {
                    if (char == '\n') lineNumber++
                    currentIndex++
                }

                char == '/' && input.getOrNull(currentIndex + 1) == '/' -> {
                    currentIndex = skipSingleLineComment(input, currentIndex)
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
                    val (token, newIndex, error) = processString(input, currentIndex, lineNumber)
                    token?.let(tokens::add)
                    error?.let(errors::add)
                    currentIndex = newIndex
                }

                char.isDigit() -> {
                    val (token, newIndex, error) = processNumber(input, currentIndex, lineNumber)
                    token?.let(tokens::add)
                    error?.let(errors::add)
                    currentIndex = newIndex
                }

                char.isLetter() || char == '_' -> {
                    val (token, newIndex) = processIdentifierOrKeyword(input, currentIndex)
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

    private fun skipSingleLineComment(
        input: String,
        currentIndex: Int,
    ): Int =
        input
            .indexOf('\n', currentIndex)
            .takeIf { it != -1 }
            ?: input.length

    private fun processString(
        input: String,
        startIndex: Int,
        lineNumber: Int,
    ): ProcessingResult {
        val endIndex =
            (startIndex + 1 until input.length)
                .find { input[it] == '"' || input[it] == '\n' }
                ?: input.length
        return when {
            endIndex >= input.length || input[endIndex] == '\n' ->
                ProcessingResult(null, endIndex, "[line $lineNumber] Error: Unterminated string.")

            else ->
                ProcessingResult(
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
    ): ProcessingResult {
        var currentIndex = startIndex

        while (
            currentIndex < input.length &&
            (input[currentIndex].isDigit() || input[currentIndex] == '.')
        ) {
            currentIndex++
        }

        val lexeme = input.substring(startIndex, currentIndex)

        if (lexeme.count { it == '.' } > 1) {
            return ProcessingResult(
                token = null,
                newIndex = currentIndex,
                error = "[line $lineNumber] Error: Unexpected character: .",
            )
        }

        val numericValue = lexeme.toDoubleOrNull()
        return ProcessingResult(
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
            '=' to MultiCharToken(TokenType.EQUAL_EQUAL, '='),
            '!' to MultiCharToken(TokenType.BANG_EQUAL, '='),
            '<' to MultiCharToken(TokenType.LESS_EQUAL, '='),
            '>' to MultiCharToken(TokenType.GREATER_EQUAL, '='),
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

data class ProcessingResult(
    val token: Token?,
    val newIndex: Int,
    val error: String?,
)

data class IdentifierProcessingResult(
    val token: Token,
    val newIndex: Int,
)

data class MultiCharToken(
    val tokenType: TokenType,
    val secondChar: Char,
)
