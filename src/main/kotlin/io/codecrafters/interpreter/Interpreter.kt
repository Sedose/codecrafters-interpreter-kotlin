package io.codecrafters.interpreter

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.normalized
import io.codecrafters.parser.Expr

class Interpreter {
  fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Literal -> expression.value.normalized()
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Unary -> evaluateUnary(expression)
      is Expr.Binary -> evaluateBinary(expression)
    }

  private fun evaluateBinary(binaryExpression: Expr.Binary): Any? {
    val (leftExpression, operatorToken, rightExpression) = binaryExpression

    val leftOperand = evaluate(leftExpression)
    val rightOperand = evaluate(rightExpression)

    val leftNumber = requireNumber(leftOperand, operatorToken)
    val rightNumber = requireNumber(rightOperand, operatorToken)

    val rawResult =
      when (operatorToken.type) {
        TokenType.STAR -> leftNumber * rightNumber
        TokenType.SLASH -> leftNumber / rightNumber
        else -> throw IllegalStateException("Unexpected operator '${operatorToken.lexeme}'.")
      }

    return rawResult.normalized()
  }

  private fun requireNumber(
    value: Any?,
    operator: Token,
  ): Double {
    if (value !is Number) {
      throw IllegalArgumentException("Operand for '${operator.lexeme}' must be a number.")
    }
    return value.toDouble()
  }

  private fun evaluateUnary(unaryExpression: Expr.Unary): Any? {
    val operandValue = evaluate(unaryExpression.right)

    return when (unaryExpression.operator.type) {
      TokenType.MINUS ->
        when (operandValue) {
          is Double -> -operandValue
          is Int -> -operandValue
          else -> throw IllegalArgumentException("Operand must be a number.")
        }
      TokenType.BANG -> !isTruthy(operandValue)
      else -> throw IllegalStateException("Unexpected unary operator ${unaryExpression.operator.lexeme}.")
    }
  }

  private fun isTruthy(value: Any?): Boolean =
    when (value) {
      null -> false
      is Boolean -> value
      else -> true
    }
}
