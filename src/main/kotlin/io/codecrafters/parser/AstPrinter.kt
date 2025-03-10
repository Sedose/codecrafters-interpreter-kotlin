package io.codecrafters.parser

class AstPrinter {
  fun print(expr: Expr): String =
    when (expr) {
      is Expr.Literal -> literalToString(expr.value)
      is Expr.Unary -> parenthesize(expr.operator.lexeme, expr.right)
      is Expr.Binary -> parenthesize(expr.operator.lexeme, expr.left, expr.right)
      is Expr.Grouping -> parenthesize("group", expr.expression)
    }

  private fun parenthesize(
    name: String,
    vararg exprs: Expr,
  ): String {
    val builder = StringBuilder()
    builder.append("(").append(name)
    exprs.forEach { builder.append(" ").append(print(it)) }
    builder.append(")")
    return builder.toString()
  }

  private fun literalToString(value: Any?): String =
    when (value) {
      null -> "nil"
      is Boolean -> value.toString() // "true" or "false"
      else -> value.toString() // e.g. "2.0" for a number
    }
}
