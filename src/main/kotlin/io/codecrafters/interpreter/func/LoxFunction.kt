package io.codecrafters.interpreter.func

import io.codecrafters.interpreter.Environment
import io.codecrafters.interpreter.StatementExecutor
import io.codecrafters.model.Stmt

class LoxFunction(
  private val declaration: Stmt.Function,
  private val closure: Environment,
  private val statementExecutor: StatementExecutor,
) : LoxCallable {
  override fun call(arguments: List<Any?>): Any? {
    val callEnvironment = Environment(closure)
    statementExecutor.interpret(declaration.body, callEnvironment)
    return null
  }

  override fun toString(): String = "<fn ${declaration.name.lexeme}>"
}
