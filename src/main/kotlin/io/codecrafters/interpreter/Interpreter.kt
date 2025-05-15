package io.codecrafters.interpreter

import io.codecrafters.model.Stmt

class Interpreter(
  private val evaluator: ExpressionEvaluator,
  private val executor: StatementExecutor,
) {
  private val globalEnvironment = Environment()

  fun evaluate(expression: io.codecrafters.model.Expr): Any? =
    with(globalEnvironment) {
      evaluator.evaluate(expression)
    }

  fun interpret(statements: List<Stmt>) = executor.interpret(statements, globalEnvironment)
}
