package io.codecrafters.interpreter

import io.codecrafters.model.StdoutSink
import io.codecrafters.model.Stmt
import io.codecrafters.toLoxString

class StatementExecutor(
  private val evaluator: ExpressionEvaluator,
  private val output: StdoutSink,
) {
  fun interpret(
    statements: List<Stmt>,
    environment: Environment,
  ) {
    statements.forEach { execute(it, environment) }
  }

  private fun execute(
    statement: Stmt,
    environment: Environment,
  ) {
    when (statement) {
      is Stmt.Expression -> with(environment) { evaluator.evaluate(statement.expression) }
      is Stmt.Print -> with(environment) { output.write(evaluator.evaluate(statement.expression).toLoxString()) }
      is Stmt.Var -> {
        val value = statement.initializer?.let { with(environment) { evaluator.evaluate(it) } }
        environment.define(statement.name.lexeme, value)
      }
      is Stmt.Block -> {
        val innerEnvironment = Environment(environment)
        interpret(statement.statements, innerEnvironment)
      }
    }
  }
}
