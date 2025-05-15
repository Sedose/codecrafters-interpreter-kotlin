package io.codecrafters.interpreter

import io.codecrafters.model.StdoutSink
import io.codecrafters.model.Stmt
import io.codecrafters.toLoxString

class StatementExecutor(
  private val environment: Environment,
  private val evaluator: ExpressionEvaluator,
  private val output: StdoutSink,
) {
  fun interpret(statements: List<Stmt>) {
    for (statement in statements) {
      execute(statement)
    }
  }

  private fun execute(statement: Stmt) {
    when (statement) {
      is Stmt.Expression -> evaluator.evaluate(statement.expression)
      is Stmt.Print -> output.write(evaluator.evaluate(statement.expression).toLoxString())
      is Stmt.Var -> {
        val value = statement.initializer?.let(evaluator::evaluate) ?: "nil"
        environment.define(statement.name.lexeme, value)
      }
      is Stmt.Block -> {
        for (nested in statement.statements) {
          execute(nested)
        }
      }
    }
  }
}
