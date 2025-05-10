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
