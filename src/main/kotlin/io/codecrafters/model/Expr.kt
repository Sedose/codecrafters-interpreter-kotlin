package io.codecrafters.model

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

  data class Binary(
    val left: Expr,
    val operator: Token,
    val right: Expr,
  ) : Expr()

  data class Variable(
    val name: Token,
  ) : Expr()

  data class Assign(
    val name: Token,
    val value: Expr,
  ) : Expr()

  data class Logical(
    val left: Expr,
    val operator: Token,
    val right: Expr,
  ) : Expr()

  data class Call(
    val callee: Expr,
    val paren: Token,
    val arguments: List<Expr>,
  ) : Expr()
}
