package io.codecrafters.model

enum class Command {
  TOKENIZE,
  PARSE,
  ;

  companion object {
    fun parse(command: String): Command? = values().find { it.name.equals(command, ignoreCase = true) }
  }
}
