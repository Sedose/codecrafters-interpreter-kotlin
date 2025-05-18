package io.codecrafters.interpreter

import io.codecrafters.isTruthy
import io.codecrafters.model.Expr
import io.codecrafters.model.LoxCallable
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.error.InterpreterException
import io.codecrafters.normalized

class ExpressionEvaluator {

  context(environment: Environment)
  fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Logical -> evaluateLogical(expression)
      is Expr.Literal -> expression.value.normalized()
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Unary -> evaluateUnary(expression)
      is Expr.Binary -> evaluateBinary(expression)
      is Expr.Variable -> environment.get(expression.name)
      is Expr.Assign -> environment.assign(expression.name, evaluate(expression.value))
      is Expr.Call    -> evaluateCall(expression)
    }

  context(_: Environment)
  private fun evaluateCall(expr: Expr.Call): Any? {
    val calleeValue = evaluate(expr.callee)
    if (calleeValue !is LoxCallable)
      throw InterpreterException("Can only call functions.", expr.paren.lineNumber)
    val evaluatedArguments = expr.arguments.map { evaluate(it) }
    return calleeValue.call(evaluatedArguments)
  }

  context(_: Environment)
  private fun evaluateLogical(expr: Expr.Logical): Any? {
    val left = evaluate(expr.left)
    return when (expr.operator.type) {
      TokenType.OR  -> if (left.isTruthy())  left else evaluate(expr.right)
      TokenType.AND -> if (!left.isTruthy()) left else evaluate(expr.right)
      else -> error("Unexpected logical operator ${expr.operator.lexeme}")
    }
  }

  context(_: Environment)
  private fun evaluateUnary(expr: Expr.Unary): Any? {
    val right = evaluate(expr.right)
    return when (expr.operator.type) {
      TokenType.MINUS -> (-requireNumber(right, expr.operator)).normalized()
      TokenType.BANG -> !right.isTruthy()
      else -> error("Unexpected unary operator ${expr.operator.lexeme}.")
    }
  }

//  @Suppress("ReturnCount")
  context(_: Environment)
  private fun evaluateBinary(expr: Expr.Binary): Any? {
    val left = evaluate(expr.left)
    val right = evaluate(expr.right)

    if (expr.operator.type == TokenType.PLUS && left is String && right is String) {
      return left + right
    }

    OperationRegistry.arithmetic[expr.operator.type]?.let { op ->
      return op(requireNumber(left, expr.operator), requireNumber(right, expr.operator)).normalized()
    }
    OperationRegistry.comparison[expr.operator.type]?.let { op ->
      return op(requireNumber(left, expr.operator), requireNumber(right, expr.operator))
    }
    OperationRegistry.equality[expr.operator.type]?.let { op ->
      return op(left, right)
    }
    error("Unexpected operator '${expr.operator.lexeme}'.")
  }

  private fun requireNumber(value: Any?, token: Token): Double =
    (value as? Number)?.toDouble()
      ?: throw InterpreterException("Operand must be a number.", token.lineNumber)
}
