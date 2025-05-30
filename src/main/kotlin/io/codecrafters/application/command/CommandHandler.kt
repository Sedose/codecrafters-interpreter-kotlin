package io.codecrafters.application.command

import io.codecrafters.model.Token

interface CommandHandler {
  fun handle(
    tokens: List<Token>,
    errors: List<String>,
  )
}
