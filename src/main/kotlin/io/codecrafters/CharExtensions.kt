package io.codecrafters

fun Char.isIdentifierChar() = isLetterOrDigit() || this == '_'

fun Char.isNumberChar() = isDigit() || this == '.'
