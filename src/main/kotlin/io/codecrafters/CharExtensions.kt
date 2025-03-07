package io.codecrafters

fun Char.isIdentifierChar() = isLetterOrDigit() || this == '_'

fun Char.isNumberChar() = this in '0'..'9' || this == '.'
