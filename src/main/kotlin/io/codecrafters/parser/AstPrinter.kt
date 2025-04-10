package io.codecrafters.parser

class AstPrinter {
  fun print(expr: Expr): String =
    when (expr) {
      is Expr.Literal -> expr.value?.toString() ?: "nil"
      is Expr.Grouping -> "(group ${print(expr.expression)})"
      is Expr.Unary -> "(${expr.operator.lexeme} ${print(expr.right)})"
    }
}
