package io.codecrafters

fun Any?.normalized(): Any? =
  when (this) {
    is Double -> if (this % 1 == 0.0) this.toInt() else this
    else -> this
  }
