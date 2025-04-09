package io.codecrafters.parser

class AstPrinter {
  fun print(expr: Expr): String =
    when (expr) {
      is Expr.Literal -> expr.value?.toString() ?: "nil"
    }
}
