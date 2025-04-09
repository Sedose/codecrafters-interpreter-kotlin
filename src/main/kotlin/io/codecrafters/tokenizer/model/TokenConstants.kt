package io.codecrafters.tokenizer.model

val RESERVED_WORDS =
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

val SINGLE_CHAR_TOKENS =
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

val MULTI_CHAR_TOKENS =
  mapOf(
    ('=' to '=') to TokenType.EQUAL_EQUAL,
    ('!' to '=') to TokenType.BANG_EQUAL,
    ('<' to '=') to TokenType.LESS_EQUAL,
    ('>' to '=') to TokenType.GREATER_EQUAL,
  )
