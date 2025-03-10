package io.codecrafters.parser

import io.codecrafters.tokenizer.model.Token
import io.codecrafters.tokenizer.model.TokenType

fun parseTokens(tokens: List<Token>): ParseResult {
  return parseExpression(tokens, 0)
}

fun parseExpression(tokens: List<Token>, startIndex: Int): ParseResult {
  return parseEquality(tokens, startIndex)
}

fun parseEquality(tokens: List<Token>, startIndex: Int): ParseResult {
  val leftResult = parseComparison(tokens, startIndex)
  if (leftResult.hadError) return leftResult
  var (expr, currentIndex, _) = leftResult
  while (match(tokens, currentIndex, TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
    val operator = tokens[currentIndex]
    val (rightExpr, nextIndex, rightError) = parseComparison(tokens, currentIndex + 1)
    if (rightError) return ParseResult(null, nextIndex, true)
    expr = Expr.Binary(expr!!, operator, rightExpr!!)
    currentIndex = nextIndex
  }
  return ParseResult(expr, currentIndex, false)
}

fun parseComparison(tokens: List<Token>, startIndex: Int): ParseResult {
  val leftResult = parseTerm(tokens, startIndex)
  if (leftResult.hadError) return leftResult
  var (expr, currentIndex, _) = leftResult
  while (match(tokens, currentIndex, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
    val operator = tokens[currentIndex]
    val (rightExpr, nextIndex, rightError) = parseTerm(tokens, currentIndex + 1)
    if (rightError) return ParseResult(null, nextIndex, true)
    expr = Expr.Binary(expr!!, operator, rightExpr!!)
    currentIndex = nextIndex
  }
  return ParseResult(expr, currentIndex, false)
}

fun parseTerm(tokens: List<Token>, startIndex: Int): ParseResult {
  val leftResult = parseFactor(tokens, startIndex)
  if (leftResult.hadError) return leftResult
  var (expr, currentIndex, _) = leftResult
  while (match(tokens, currentIndex, TokenType.MINUS, TokenType.PLUS)) {
    val operator = tokens[currentIndex]
    val (rightExpr, nextIndex, rightError) = parseFactor(tokens, currentIndex + 1)
    if (rightError) return ParseResult(null, nextIndex, true)
    expr = Expr.Binary(expr!!, operator, rightExpr!!)
    currentIndex = nextIndex
  }
  return ParseResult(expr, currentIndex, false)
}

fun parseFactor(tokens: List<Token>, startIndex: Int): ParseResult {
  val leftResult = parseUnary(tokens, startIndex)
  if (leftResult.hadError) return leftResult
  var (expr, currentIndex, _) = leftResult
  while (match(tokens, currentIndex, TokenType.SLASH, TokenType.STAR)) {
    val operator = tokens[currentIndex]
    val (rightExpr, nextIndex, rightError) = parseUnary(tokens, currentIndex + 1)
    if (rightError) return ParseResult(null, nextIndex, true)
    expr = Expr.Binary(expr!!, operator, rightExpr!!)
    currentIndex = nextIndex
  }
  return ParseResult(expr, currentIndex, false)
}

fun parseUnary(tokens: List<Token>, startIndex: Int): ParseResult {
  if (match(tokens, startIndex, TokenType.BANG, TokenType.MINUS)) {
    val operator = tokens[startIndex]
    val (rightExpr, nextIndex, rightError) = parseUnary(tokens, startIndex + 1)
    if (rightError) return ParseResult(null, nextIndex, true)
    return ParseResult(Expr.Unary(operator, rightExpr!!), nextIndex, false)
  }
  return parsePrimary(tokens, startIndex)
}

fun parsePrimary(tokens: List<Token>, startIndex: Int): ParseResult {
  if (outOfBounds(tokens, startIndex)) return ParseResult(null, startIndex, true)
  val token = tokens[startIndex]
  if (token.type == TokenType.FALSE) return ParseResult(Expr.Literal(false), startIndex + 1, false)
  if (token.type == TokenType.TRUE) return ParseResult(Expr.Literal(true), startIndex + 1, false)
  if (token.type == TokenType.NIL) return ParseResult(Expr.Literal(null), startIndex + 1, false)
  if (token.type == TokenType.NUMBER || token.type == TokenType.STRING) {
    return ParseResult(Expr.Literal(token.literal), startIndex + 1, false)
  }
  if (token.type == TokenType.LEFT_PAREN) {
    val (expr, nextIndex, error) = parseExpression(tokens, startIndex + 1)
    if (error) return ParseResult(null, nextIndex, true)
    if (outOfBounds(tokens, nextIndex) || tokens[nextIndex].type != TokenType.RIGHT_PAREN) {
      return ParseResult(null, nextIndex, true)
    }
    return ParseResult(Expr.Grouping(expr!!), nextIndex + 1, false)
  }
  return ParseResult(null, startIndex, true)
}

fun match(tokens: List<Token>, index: Int, vararg types: TokenType): Boolean {
  if (outOfBounds(tokens, index)) return false
  return types.contains(tokens[index].type)
}

fun outOfBounds(tokens: List<Token>, index: Int): Boolean {
  return index !in tokens.indices
}
