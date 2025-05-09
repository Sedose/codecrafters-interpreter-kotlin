package io.codecrafters.model.error

class UnknownCommandException(
  commandString: String,
) : RuntimeException("Unknown command: $commandString")
