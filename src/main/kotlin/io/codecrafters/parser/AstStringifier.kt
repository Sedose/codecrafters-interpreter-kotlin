package io.codecrafters.parser

class AstStringifier {
  fun stringify(expr: Expr): String =
    when (expr) {
      is Expr.Literal -> {
        when (val value = expr.value) {
          null -> "nil"
          else -> value.toString()
        }
      }
      is Expr.Grouping -> "(group ${stringify(expr.expression)})"
      is Expr.Unary -> "(${expr.operator.lexeme} ${stringify(expr.right)})"
      is Expr.Binary -> "(${expr.operator.lexeme} ${stringify(expr.left)} ${stringify(expr.right)})"
    }
}
