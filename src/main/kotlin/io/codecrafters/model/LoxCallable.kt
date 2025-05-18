package io.codecrafters.model

fun interface LoxCallable {
  fun call(arguments: List<Any?>): Any?
}
