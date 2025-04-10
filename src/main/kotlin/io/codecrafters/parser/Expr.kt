package io.codecrafters.parser

import io.codecrafters.tokenizer.model.Token

sealed class Expr {
  data class Literal(
    val value: Any?,
  ) : Expr()

  data class Grouping(
    val expression: Expr,
  ) : Expr()

  data class Unary(
    val operator: Token,
    val right: Expr,
  ) : Expr()
}
