package io.codecrafters.parser

import io.codecrafters.model.Expr
import org.springframework.stereotype.Component

@Component
class AstStringifier {
  fun stringify(expr: Expr): String =
    when (expr) {
      is Expr.Literal -> {
        when (val value = expr.value) {
          null -> "nil"
          else -> value.toString()
        }
      }
      is Expr.Grouping -> "(group ${stringify(expr.expression)})"
      is Expr.Unary -> "(${expr.operator.lexeme} ${stringify(expr.right)})"
      is Expr.Binary -> "(${expr.operator.lexeme} ${stringify(expr.left)} ${stringify(expr.right)})"
      is Expr.Variable -> expr.name.lexeme
      is Expr.Assign -> "(= ${expr.name.lexeme} ${stringify(expr.value)})"
      is Expr.Logical -> "(${expr.operator.lexeme} ${stringify(expr.left)} ${stringify(expr.right)})"
      is Expr.Call -> {
        val args = expr.arguments.joinToString(" ") { stringify(it) }
        "(call ${stringify(expr.callee)} $args)"
      }
    }
}
