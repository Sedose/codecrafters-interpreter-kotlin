package io.codecrafters.interpreter

import io.codecrafters.interpreter.func.ClockNativeFunction
import io.codecrafters.model.Stmt
import org.springframework.stereotype.Component

@Component
class Interpreter(
  private val evaluator: ExpressionEvaluator,
  private val executor: StatementExecutor,
) {
  private val globalEnvironment =
    Environment().apply {
      define("clock", ClockNativeFunction)
    }

  fun evaluate(expression: io.codecrafters.model.Expr): Any? =
    with(globalEnvironment) {
      evaluator.evaluate(expression)
    }

  fun interpret(statements: List<Stmt>) = executor.interpret(statements, globalEnvironment)
}
