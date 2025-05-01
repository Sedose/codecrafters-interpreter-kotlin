package io.codecrafters.model

data class InterpreterError(
  val message: String,
  val lineNumber: Int,
)
