package io.codecrafters.interpreter

import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.normalized
import io.codecrafters.parser.Expr

class Interpreter {
  private val arithmeticOperations:
    Map<TokenType, (Double, Double) -> Double> =
    mapOf(
      TokenType.PLUS to Double::plus,
      TokenType.MINUS to Double::minus,
      TokenType.STAR to Double::times,
      TokenType.SLASH to Double::div,
    )

  private val comparisonOperations:
    Map<TokenType, (Double, Double) -> Boolean> =
    mapOf(
      TokenType.GREATER to { a, b -> a > b },
      TokenType.GREATER_EQUAL to { a, b -> a >= b },
      TokenType.LESS to { a, b -> a < b },
      TokenType.LESS_EQUAL to { a, b -> a <= b },
    )

  fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Literal -> expression.value.normalized()
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Unary -> evaluateUnary(expression)
      is Expr.Binary -> evaluateBinary(expression)
    }

  private fun evaluateBinary(binaryExpression: Expr.Binary): Any? {
    val (leftExpression, operatorToken, rightExpression) = binaryExpression
    val leftValue = evaluate(leftExpression)
    val rightValue = evaluate(rightExpression)

    if (operatorToken.type == TokenType.PLUS &&
      leftValue is String &&
      rightValue is String
    ) {
      return leftValue + rightValue
    }

    arithmeticOperations[operatorToken.type]?.let { operation ->
      return applyBinaryOperation(leftValue, rightValue, operatorToken, operation).normalized()
    }

    comparisonOperations[operatorToken.type]?.let { operation ->
      return applyBinaryOperation(leftValue, rightValue, operatorToken, operation)
    }

    throw IllegalStateException("Unexpected operator '${operatorToken.lexeme}'.")
  }

  private fun evaluateUnary(unaryExpression: Expr.Unary): Any? {
    val operandValue = evaluate(unaryExpression.right)

    return when (unaryExpression.operator.type) {
      TokenType.MINUS ->
        when (operandValue) {
          is Number -> (-operandValue.toDouble()).normalized()
          else -> throw IllegalArgumentException("Operand must be a number.")
        }
      TokenType.BANG -> !isTruthy(operandValue)
      else -> throw IllegalStateException("Unexpected unary operator ${unaryExpression.operator.lexeme}.")
    }
  }

  private fun <T> applyBinaryOperation(
    leftValue: Any?,
    rightValue: Any?,
    operatorToken: Token,
    operation: (Double, Double) -> T,
  ): T {
    val leftNumber = requireNumber(leftValue, operatorToken)
    val rightNumber = requireNumber(rightValue, operatorToken)
    return operation(leftNumber, rightNumber)
  }

  private fun requireNumber(
    value: Any?,
    operatorToken: Token,
  ): Double =
    (value as? Number)
      ?.toDouble()
      ?: throw IllegalArgumentException("Operand for '${operatorToken.lexeme}' must be a number.")

  private fun isTruthy(value: Any?): Boolean =
    when (value) {
      null -> false
      is Boolean -> value
      else -> true
    }
}
