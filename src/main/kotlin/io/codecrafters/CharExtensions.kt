package io.codecrafters

fun Char?.isIdentifierChar() = this != null && (isLetterOrDigit() || this == '_')
