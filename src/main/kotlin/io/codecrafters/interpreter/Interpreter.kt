package io.codecrafters.interpreter

import io.codecrafters.model.TokenType
import io.codecrafters.normalized
import io.codecrafters.parser.Expr

class Interpreter {
  fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Literal -> expression.value.normalized()
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Unary -> evaluateUnary(expression)
      is Expr.Binary -> TODO("Binary not yet implemented")
    }

  private fun evaluateUnary(expression: Expr.Unary): Any? {
    val operandValue = evaluate(expression.right)

    return when (expression.operator.type) {
      TokenType.MINUS -> when (operandValue) {
        is Double -> -operandValue
        is Int    -> -operandValue
        else      -> throw IllegalArgumentException("Operand must be a number.")
      }
      TokenType.BANG  -> !isTruthy(operandValue)
      else            ->
        throw IllegalStateException("Unexpected unary operator ${expression.operator.lexeme}.")
    }
  }

  private fun isTruthy(value: Any?): Boolean =
    when (value) {
      null        -> false
      is Boolean  -> value
      else        -> true
    }
}
