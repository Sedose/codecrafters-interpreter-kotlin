package io.codecrafters.interpreter

import io.codecrafters.model.Token
import io.codecrafters.model.error.InterpreterException

class Environment(
  private val enclosingEnvironment: Environment? = null,
) {
  private val bindings = mutableMapOf<String, Any?>()

  fun define(
    name: String,
    value: Any?,
  ) {
    bindings[name] = value
  }

  fun assign(
    name: Token,
    value: Any?,
  ): Any? =
    when {
      bindings.containsKey(name.lexeme) -> {
        bindings[name.lexeme] = value
        value
      }
      enclosingEnvironment != null -> enclosingEnvironment.assign(name, value)
      else -> throw InterpreterException("Undefined variable '${name.lexeme}'.", name.lineNumber)
    }

  fun get(name: Token): Any? =
    bindings[name.lexeme]
      ?: enclosingEnvironment?.get(name)
      ?: throw InterpreterException("Undefined variable '${name.lexeme}'.", name.lineNumber)
}
