package io.codecrafters.model

enum class Command {
  TOKENIZE,
  PARSE,
  EVALUATE,
  RUN,
  ;

  companion object {
    fun parse(command: String): Command? = entries.find { it.name.equals(command, ignoreCase = true) }
  }
}
