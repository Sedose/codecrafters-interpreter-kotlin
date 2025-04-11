package io.codecrafters.parser

class AstStringifier {
  fun stringifyExpression(expr: Expr): String =
    when (expr) {
      is Expr.Literal -> {
        when (val value = expr.value) {
          null -> "nil"
          else -> value.toString()
        }
      }
      is Expr.Grouping -> "(group ${stringifyExpression(expr.expression)})"
      is Expr.Unary -> "(${expr.operator.lexeme} ${stringifyExpression(expr.right)})"
      is Expr.Binary -> "(${expr.operator.lexeme} ${stringifyExpression(expr.left)} ${stringifyExpression(expr.right)})"
    }
}
