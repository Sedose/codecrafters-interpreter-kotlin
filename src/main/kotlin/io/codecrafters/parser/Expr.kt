package io.codecrafters.parser

sealed class Expr {
  data class Literal(
    val value: Any?,
  ) : Expr()
}
