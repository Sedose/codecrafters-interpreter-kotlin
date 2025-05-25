package io.codecrafters.interpreter

import io.codecrafters.model.TokenType
import org.springframework.stereotype.Component

@Component
class OperationRegistry {
  val arithmetic: Map<TokenType, (Double, Double) -> Double> =
    mapOf(
      TokenType.PLUS to Double::plus,
      TokenType.MINUS to Double::minus,
      TokenType.STAR to Double::times,
      TokenType.SLASH to Double::div,
    )

  val comparison: Map<TokenType, (Double, Double) -> Boolean> =
    mapOf(
      TokenType.GREATER to { a, b -> a > b },
      TokenType.GREATER_EQUAL to { a, b -> a >= b },
      TokenType.LESS to { a, b -> a < b },
      TokenType.LESS_EQUAL to { a, b -> a <= b },
    )

  val equality: Map<TokenType, (Any?, Any?) -> Boolean> =
    mapOf(
      TokenType.EQUAL_EQUAL to { a, b -> a == b },
      TokenType.BANG_EQUAL to { a, b -> a != b },
    )
}
