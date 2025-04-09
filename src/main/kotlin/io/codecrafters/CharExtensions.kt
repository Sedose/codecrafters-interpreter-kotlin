package io.codecrafters

fun Char.isIdentifierChar() = isLetterOrDigit() || this == '_'
