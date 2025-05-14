package io.codecrafters.interpreter

import io.codecrafters.model.Expr
import io.codecrafters.model.Stmt

class Interpreter(
  private val evaluator: ExpressionEvaluator,
  private val executor: StatementExecutor,
) {
  fun evaluate(expression: Expr): Any? = evaluator.evaluate(expression)

  fun interpret(statements: List<Stmt>) = executor.interpret(statements)
}
