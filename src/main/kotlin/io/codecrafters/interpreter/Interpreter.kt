package io.codecrafters.interpreter

import io.codecrafters.parser.Expr

class Interpreter {
  fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Literal -> expression.value.normalized()
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Unary -> TODO("Unary not yet implemented")
      is Expr.Binary -> TODO("Binary not yet implemented")
    }

  private fun Any?.normalized(): Any? =
    when (this) {
      is Double -> if (this % 1 == 0.0) this.toInt() else this
      else -> this
    }
}
