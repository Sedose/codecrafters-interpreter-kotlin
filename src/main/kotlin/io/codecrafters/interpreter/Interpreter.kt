package io.codecrafters.interpreter

import io.codecrafters.parser.Expr

class Interpreter {
  fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Literal -> expression.value
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Unary -> TODO("Unary not yet implemented")
      is Expr.Binary -> TODO("Binary not yet implemented")
    }
}
