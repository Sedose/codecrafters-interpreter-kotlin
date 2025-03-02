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

                char in tokenMap -> {
                    tokens.add(Token(tokenMap[char]!!, char.toString()))
                    current++
                }

                char in multiCharTokens.keys && input.getOrNull(current + 1) == multiCharTokens[char]?.second -> {
                    val (tokenType, secondChar) = multiCharTokens[char]!!
                    tokens.add(Token(tokenType, "$char$secondChar"))
                    current += 2
                }

                char == '"' -> {
                    val (token, newIndex, error) = processString(input, current, lineNumber)
                    token?.let { tokens.add(it) }
                    error?.let { errors.add(it) }
                    current = newIndex
                }

                char.isDigit() -> {
                    val (token, newIndex, error) = processNumber(input, current, lineNumber)
                    token?.let { tokens.add(it) }
                    error?.let { errors.add(it) }
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
    ): Int {
        var index = current
        while (index < input.length && input[index] != '\n') {
            index++
        }
        return index
    }

    private fun processString(
        input: String,
        startIndex: Int,
        lineNumber: Int,
    ): Triple<Token?, Int, String?> {
        var index = startIndex + 1
        while (index < input.length && input[index] != '"') {
            if (input[index] == '\n') return Triple(null, index, "[line $lineNumber] Error: Unterminated string.")
            index++
        }

        return if (index >= input.length) {
            Triple(null, index, "[line $lineNumber] Error: Unterminated string.")
        } else {
            val lexeme = input.substring(startIndex, index + 1)
            val literal = input.substring(startIndex + 1, index)
            Triple(Token(TokenType.STRING, lexeme, literal), index + 1, null)
        }
    }

    private fun processNumber(
        input: String,
        startIndex: Int,
        lineNumber: Int,
    ): Triple<Token?, Int, String?> {
        var index = startIndex
        var dotCount = 0

        while (index < input.length && (input[index].isDigit() || input[index] == '.')) {
            if (input[index] == '.') {
                dotCount++
                if (dotCount > 1) return Triple(null, index, "[line $lineNumber] Error: Unexpected character: .")
            }
            index++
        }

        val lexeme = input.substring(startIndex, index)
        val literal = lexeme.toDoubleOrNull()
        return Triple(Token(TokenType.NUMBER, lexeme, literal), index, null)
    }

    private fun processIdentifierOrKeyword(
        input: String,
        startIndex: Int,
    ): Pair<Token, Int> {
        var index = startIndex
        while (index < input.length && (input[index].isLetterOrDigit() || input[index] == '_')) {
            index++
        }

        val lexeme = input.substring(startIndex, index)
        val tokenType = reservedWords[lexeme] ?: TokenType.IDENTIFIER
        return Token(tokenType, lexeme) to index
    }

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
            '/' to TokenType.SLASH,
            '=' to TokenType.EQUAL,
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
