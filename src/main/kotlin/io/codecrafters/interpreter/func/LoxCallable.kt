package io.codecrafters.interpreter.func

fun interface LoxCallable {
  fun call(passedArguments: List<Any?>): Any?
}
