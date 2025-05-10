package io.codecrafters.model

sealed class Stmt {
  data class Expression(
    val expression: Expr,
  ) : Stmt()

  data class Print(
    val expression: Expr,
  ) : Stmt()
}
