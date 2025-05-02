package io.codecrafters.interpreter

import arrow.core.raise.Raise
import io.codecrafters.model.error.InterpreterError
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.TokenType.*
import io.codecrafters.normalized
import io.codecrafters.parser.Expr

class Interpreter {
  private val arithmeticOperations:
    Map<TokenType, (Double, Double) -> Double> =
    mapOf(
      PLUS to Double::plus,
      MINUS to Double::minus,
      STAR to Double::times,
      SLASH to Double::div,
    )

  private val comparisonOperations:
    Map<TokenType, (Double, Double) -> Boolean> =
    mapOf(
      GREATER to { a, b -> a > b },
      GREATER_EQUAL to { a, b -> a >= b },
      LESS to { a, b -> a < b },
      LESS_EQUAL to { a, b -> a <= b },
    )

  private val equalityOperations:
    Map<TokenType, (Any?, Any?) -> Boolean> =
    mapOf(
      EQUAL_EQUAL to { a, b -> a == b },
      BANG_EQUAL to { a, b -> a != b },
    )

  context(_: Raise<InterpreterError>)
  fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Literal -> expression.value.normalized()
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Unary -> evaluateUnary(expression)
      is Expr.Binary -> evaluateBinary(expression)
    }

  context(_: Raise<InterpreterError>)
  private fun evaluateBinary(binaryExpression: Expr.Binary): Any? {
    val (leftExpression, operatorToken, rightExpression) = binaryExpression
    val leftValue = evaluate(leftExpression)
    val rightValue = evaluate(rightExpression)

    if (operatorToken.type == PLUS &&
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

    equalityOperations[operatorToken.type]?.let { operation ->
      return operation(leftValue, rightValue)
    }

    throw IllegalStateException("Unexpected operator '${operatorToken.lexeme}'.")
  }

  context(raise: Raise<InterpreterError>)
  private fun evaluateUnary(unaryExpression: Expr.Unary): Any? {
    val operandValue = evaluate(unaryExpression.right)

    return when (unaryExpression.operator.type) {
      MINUS ->
        when (operandValue) {
          is Number -> (-operandValue.toDouble()).normalized()
          else -> raise.raise(InterpreterError("Operand must be a number.", unaryExpression.operator.lineNumber))
        }

      BANG -> !isTruthy(operandValue)
      else -> throw IllegalStateException("Unexpected unary operator ${unaryExpression.operator.lexeme}.")
    }
  }

  context(_: Raise<InterpreterError>)
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

  context(raise: Raise<InterpreterError>)
  private fun requireNumber(
    value: Any?,
    operatorToken: Token,
  ): Double =
    (value as? Number)
      ?.toDouble()
      ?: raise.raise(InterpreterError("Operands must be numbers.", operatorToken.lineNumber))

  private fun isTruthy(value: Any?): Boolean =
    when (value) {
      null -> false
      is Boolean -> value
      else -> true
    }
}
