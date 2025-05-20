package io.codecrafters.interpreter.func

import io.codecrafters.interpreter.Environment
import io.codecrafters.interpreter.StatementExecutor
import io.codecrafters.model.Stmt
import io.codecrafters.model.error.InterpreterException

class LoxFunction(
  private val declaration: Stmt.Function,
  private val closure: Environment,
  private val statementExecutor: StatementExecutor,
) : LoxCallable {
  override fun call(arguments: List<Any?>): Any? {
    if (arguments.size != declaration.parameters.size) {
      throw InterpreterException(
        "Expected ${declaration.parameters.size} arguments but got ${arguments.size}.",
        declaration.name.lineNumber,
      )
    }

    val callEnvironment = Environment(closure)
    for (index in declaration.parameters.indices) {
      val parameterToken = declaration.parameters[index]
      val argumentValue = arguments[index]
      callEnvironment.define(parameterToken.lexeme, argumentValue)
    }

    statementExecutor.interpret(declaration.body, callEnvironment)
    return null
  }

  override fun toString(): String = "<fn ${declaration.name.lexeme}>"
}
