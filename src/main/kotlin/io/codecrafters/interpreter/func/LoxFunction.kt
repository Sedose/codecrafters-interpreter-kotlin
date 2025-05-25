package io.codecrafters.interpreter.func

import io.codecrafters.interpreter.Environment
import io.codecrafters.interpreter.StatementExecutor
import io.codecrafters.model.ReturnSignal
import io.codecrafters.model.Stmt
import io.codecrafters.model.error.InterpreterException

class LoxFunction(
  private val declaration: Stmt.Function,
  private val closure: Environment,
  private val statementExecutor: StatementExecutor,
) : LoxCallable {
  override fun call(passedArguments: List<Any?>): Any? {
    if (passedArguments.size != declaration.parameters.size) {
      throw InterpreterException(
        "Expected ${declaration.parameters.size} arguments but got ${passedArguments.size}.",
        declaration.name.lineNumber,
      )
    }

    val invocationEnvironment = Environment(closure)
    for ((parameterToken, argumentValue) in declaration.parameters.zip(passedArguments)) {
      invocationEnvironment.define(parameterToken.lexeme, argumentValue)
    }

    return try {
      statementExecutor.interpret(declaration.body, invocationEnvironment)
      null
    } catch (signal: ReturnSignal) {
      signal.value
    }
  }

  override fun toString(): String = "<fn ${declaration.name.lexeme}>"
}
