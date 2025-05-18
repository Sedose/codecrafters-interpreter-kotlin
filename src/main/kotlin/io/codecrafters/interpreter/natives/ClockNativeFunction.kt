package io.codecrafters.interpreter.natives

import io.codecrafters.model.LoxCallable

object ClockNativeFunction : LoxCallable {
  override fun call(arguments: List<Any?>): Any? = System.currentTimeMillis() / 1_000.0
}
