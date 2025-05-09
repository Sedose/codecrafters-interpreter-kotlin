package io.codecrafters.model.error

class InterpreterException(
  message: String,
  val lineNumber: Int,
  cause: Throwable? = null,
) : RuntimeException(message, cause)
