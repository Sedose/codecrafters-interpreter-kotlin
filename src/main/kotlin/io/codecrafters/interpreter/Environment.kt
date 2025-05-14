package io.codecrafters.interpreter

import io.codecrafters.model.Token
import io.codecrafters.model.error.InterpreterException

class Environment {
  private val variables = mutableMapOf<String, Any?>()

  fun define(
    name: String,
    value: Any?,
  ) {
    variables[name] = value
  }

  fun assign(
    name: Token,
    value: Any?,
  ): Any? {
    if (!variables.containsKey(name.lexeme)) {
      throw InterpreterException("Undefined variable '${name.lexeme}'.", name.lineNumber)
    }
    variables[name.lexeme] = value
    return value
  }

  fun get(name: Token): Any? =
    variables[name.lexeme]
      ?: throw InterpreterException("Undefined variable '${name.lexeme}'.", name.lineNumber)
}
