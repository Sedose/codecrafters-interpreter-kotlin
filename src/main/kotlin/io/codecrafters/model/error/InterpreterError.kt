package io.codecrafters.model.error

import arrow.core.raise.ExperimentalTraceApi
import arrow.core.raise.Trace

@OptIn(ExperimentalTraceApi::class)
data class InterpreterErrorWithTrace(
  val interpreterError: InterpreterError,
  val trace: Trace,
)

data class InterpreterError(
  val message: String,
  val lineNumber: Int,
)
