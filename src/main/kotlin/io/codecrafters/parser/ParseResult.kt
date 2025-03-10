package io.codecrafters.parser

data class ParseResult(
  val expr: Expr?,
  val nextIndex: Int,
  val hadError: Boolean,
)
