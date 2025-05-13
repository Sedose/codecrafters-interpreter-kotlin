package io.codecrafters.interpreter

import io.codecrafters.model.Expr
import io.codecrafters.model.StdoutSink
import io.codecrafters.model.Stmt
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.error.InterpreterException
import io.codecrafters.normalized

class Interpreter(
  private val stdout: StdoutSink,
) {
  private val globals = mutableMapOf<String, Any?>()

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

  private val equalityOperations:
    Map<TokenType, (Any?, Any?) -> Boolean> =
    mapOf(
      TokenType.EQUAL_EQUAL to { a, b -> a == b },
      TokenType.BANG_EQUAL to { a, b -> a != b },
    )

  fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Literal -> expression.value.normalized()
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Unary -> evaluateUnary(expression)
      is Expr.Binary -> evaluateBinary(expression)
      is Expr.Variable -> getVariable(expression.name)
    }

  fun interpret(statements: List<Stmt>) {
    for (stmt in statements) execute(stmt)
  }

  private fun getVariable(name: Token): Any? =
    globals[name.lexeme] ?: throw InterpreterException(
      "Undefined variable '${name.lexeme}'.",
      name.lineNumber,
    )

  private fun evaluateUnary(expr: Expr.Unary): Any? {
    val right = evaluate(expr.right)
    return when (expr.operator.type) {
      TokenType.MINUS -> {
        val num = requireNumber(right, expr.operator)
        (-num).normalized()
      }
      TokenType.BANG -> !isTruthy(right)
      else -> throw IllegalStateException("Unexpected unary operator ${expr.operator.lexeme}.")
    }
  }

  private fun evaluateBinary(expr: Expr.Binary): Any? {
    val left = evaluate(expr.left)
    val right = evaluate(expr.right)

    if (expr.operator.type == TokenType.PLUS &&
      left is String &&
      right is String
    ) {
      return left + right
    }

    arithmeticOperations[expr.operator.type]?.let { op ->
      return op(requireNumber(left, expr.operator), requireNumber(right, expr.operator)).normalized()
    }
    comparisonOperations[expr.operator.type]?.let { op ->
      return op(requireNumber(left, expr.operator), requireNumber(right, expr.operator))
    }
    equalityOperations[expr.operator.type]?.let { op ->
      return op(left, right)
    }
    throw IllegalStateException("Unexpected operator '${expr.operator.lexeme}'.")
  }

  private fun requireNumber(
    value: Any?,
    token: Token,
  ): Double =
    (value as? Number)?.toDouble()
      ?: throw InterpreterException("Operand must be a number.", token.lineNumber)

  private fun isTruthy(value: Any?): Boolean =
    when (value) {
      null -> false
      is Boolean -> value
      else -> true
    }

  private fun execute(statement: Stmt) =
    when (statement) {
      is Stmt.Expression -> evaluate(statement.expression)
      is Stmt.Print -> stdout.write(evaluate(statement.expression).toLoxString())
      is Stmt.Var -> {
        val value = statement.initializer?.let(this::evaluate) ?: "nil"
        globals[statement.name.lexeme] = value
      }
    }

  private fun Any?.toLoxString(): String =
    when (this) {
      null -> "nil"
      else -> toString()
    }
}
