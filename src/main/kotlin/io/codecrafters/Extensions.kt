package io.codecrafters

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType

fun List<Token>.withEofGuaranteed(): List<Token> =
  if (this.lastOrNull()?.type == TokenType.EOF) {
    this
  } else {
    this + Token(TokenType.EOF, "", null, -1)
  }

fun Any?.toLoxString(): String =
  when (this) {
    null -> "nil"
    else -> toString()
  }

infix fun Int.isAfter(other: Int): Boolean = this > other

fun Char?.isIdentifierChar() = this != null && (isLetterOrDigit() || this == '_')

fun Any?.normalized(): Any? =
  when (this) {
    is Double -> if (this % 1 == 0.0) this.toInt() else this
    else -> this
  }

fun Any?.isTruthy(): Boolean =
  when (this) {
    null -> false
    is Boolean -> this
    else -> true
  }
