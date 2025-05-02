package io.codecrafters.model.error

import io.codecrafters.model.Token

data class ParseError(
    val message: String,
    val token: Token,
)
