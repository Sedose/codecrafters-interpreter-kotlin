package io.codecrafters.interpreter.func

fun interface LoxCallable {
  fun call(arguments: List<Any?>): Any?
}
