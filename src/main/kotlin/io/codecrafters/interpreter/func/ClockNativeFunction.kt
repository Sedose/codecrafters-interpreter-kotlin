package io.codecrafters.interpreter.func

object ClockNativeFunction : LoxCallable {
  override fun call(passedArguments: List<Any?>): Any? = System.currentTimeMillis() / 1_000.0
}
