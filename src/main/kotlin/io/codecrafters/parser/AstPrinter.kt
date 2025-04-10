package io.codecrafters.parser

class AstPrinter {
  fun print(expr: Expr): String =
    when (expr) {
      is Expr.Literal -> {
        when (val value = expr.value) {
          null -> "nil"
          is Double -> "%.2f".format(value).trimEnd('0').trimEnd('.')
          else -> value.toString()
        }
      }
      is Expr.Grouping -> "(group ${print(expr.expression)})"
      is Expr.Unary -> "(${expr.operator.lexeme} ${print(expr.right)})"
      is Expr.Binary -> "(${expr.operator.lexeme} ${print(expr.left)} ${print(expr.right)})"
    }
}
