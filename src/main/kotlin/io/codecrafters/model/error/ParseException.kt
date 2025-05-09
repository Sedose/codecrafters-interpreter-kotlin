package io.codecrafters.model.error

import io.codecrafters.model.Token

class ParseException(
  message: String,
  val token: Token,
  cause: Throwable? = null,
) : RuntimeException(message, cause)
