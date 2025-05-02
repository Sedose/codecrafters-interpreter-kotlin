package io.codecrafters.model.error

data class InterpreterError(
  val message: String,
  val lineNumber: Int,
)
