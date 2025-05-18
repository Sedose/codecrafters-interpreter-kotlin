package io.codecrafters.parser

import io.codecrafters.model.Expr
import io.codecrafters.model.Stmt
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.error.ParseException

private val UNARY_TOKEN_TYPES = setOf(TokenType.BANG, TokenType.MINUS)
private val EQUALITY_OPERATORS = setOf(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)
private val COMPARISON_OPERATORS =
  setOf(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)
private val ADDITIVE_OPERATORS = setOf(TokenType.PLUS, TokenType.MINUS)
private val MULTIPLICATIVE_OPERATORS = setOf(TokenType.STAR, TokenType.SLASH)

/**
 * Hand-rolled recursive-descent parser.
 */
class Parser(
  private val tokens: List<Token>,
) {
  private var currentIndex = 0

  fun parseProgram(): List<Stmt> =
    buildList {
      while (!isAtEnd()) {
        add(parseDeclaration())
      }
    }

  private fun parseDeclaration(): Stmt =
    when {
      check(TokenType.VAR) -> {
        advance()
        parseVarDeclaration()
      }
      else -> parseStatement()
    }

  private fun parseStatement(): Stmt =
    when {
      check(TokenType.FOR) -> {
        advance()
        parseForStatement()
      }
      check(TokenType.WHILE) -> {
        advance()
        parseWhileStatement()
      }
      check(TokenType.IF) -> {
        advance()
        parseIfStatement()
      }
      check(TokenType.LEFT_BRACE) -> {
        advance()
        Stmt.Block(parseBlock())
      }
      check(TokenType.PRINT) -> {
        advance()
        parsePrintStatement()
      }
      else -> parseExpressionStatement()
    }

  private fun parseForStatement(): Stmt {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.")

    val initializer: Stmt? =
      when {
        check(TokenType.SEMICOLON) -> {
          advance()
          null
        }
        check(TokenType.VAR) -> {
          advance()
          parseVarDeclaration()
        }
        else -> parseExpressionStatement()
      }

    val condition: Expr? =
      if (!check(TokenType.SEMICOLON)) parseExpression() else null
    consume(TokenType.SEMICOLON, "Expect ';' after loop condition.")

    val increment: Expr? =
      if (!check(TokenType.RIGHT_PAREN)) parseExpression() else null
    consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.")

    var body: Stmt = parseStatement()

    if (increment != null) {
      body =
        Stmt.Block(
          listOf(
            body,
            Stmt.Expression(increment),
          ),
        )
    }

    val loopCondition = condition ?: Expr.Literal(true)
    body = Stmt.While(loopCondition, body)

    return if (initializer != null) {
      Stmt.Block(listOf(initializer, body))
    } else {
      body
    }
  }

  private fun parseWhileStatement(): Stmt {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.")
    val condition = parseExpression()
    consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.")
    val body = parseStatement()
    return Stmt.While(condition, body)
  }

  private fun parseIfStatement(): Stmt {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.")
    val condition = parseExpression()
    consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.")
    val thenBranch = parseStatement()
    val elseBranch =
      if (check(TokenType.ELSE)) {
        advance()
        parseStatement()
      } else {
        null
      }
    return Stmt.If(condition, thenBranch, elseBranch)
  }

  private fun parseBlock(): List<Stmt> =
    buildList {
      while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
        add(parseDeclaration())
      }
      consume(TokenType.RIGHT_BRACE, "Expect '}' .")
    }

  private fun parseVarDeclaration(): Stmt {
    val name = consume(TokenType.IDENTIFIER, "Expect variable name.")
    val initializer =
      if (check(TokenType.EQUAL)) {
        advance()
        parseExpression()
      } else {
        null
      }
    consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
    return Stmt.Var(name, initializer)
  }

  private fun parsePrintStatement(): Stmt {
    val value = parseExpression()
    consume(TokenType.SEMICOLON, "Expect ';' after value.")
    return Stmt.Print(value)
  }

  private fun parseExpressionStatement(): Stmt {
    val value = parseExpression()
    consume(TokenType.SEMICOLON, "Expect ';' after expression.")
    return Stmt.Expression(value)
  }

  fun parse(): Expr = parseExpression()

  private fun parseExpression(): Expr = parseAssignment()

  private fun parseAssignment(): Expr {
    val expression = parseOr()
    if (check(TokenType.EQUAL)) {
      val equalsToken = advance()
      val value = parseAssignment()
      if (expression is Expr.Variable) {
        return Expr.Assign(expression.name, value)
      }
      throw ParseException("Invalid assignment target.", equalsToken)
    }
    return expression
  }

  private fun parseOr(): Expr {
    var expr = parseAnd()
    while (check(TokenType.OR)) {
      val operator = advance()
      val right = parseAnd()
      expr = Expr.Logical(expr, operator, right)
    }
    return expr
  }

  private fun parseAnd(): Expr {
    var expr = parseEquality()
    while (check(TokenType.AND)) {
      val operator = advance()
      val right = parseEquality()
      expr = Expr.Logical(expr, operator, right)
    }
    return expr
  }

  private fun parseEquality(): Expr {
    var expr = parseComparison()
    while (check(*EQUALITY_OPERATORS.toTypedArray())) {
      val operator = advance()
      val right = parseComparison()
      expr = Expr.Binary(expr, operator, right)
    }
    return expr
  }

  private fun parseComparison(): Expr {
    var expr = parseAdditive()
    while (check(*COMPARISON_OPERATORS.toTypedArray())) {
      val operator = advance()
      val right = parseAdditive()
      expr = Expr.Binary(expr, operator, right)
    }
    return expr
  }

  private fun parseAdditive(): Expr {
    var expr = parseMultiplicative()
    while (check(*ADDITIVE_OPERATORS.toTypedArray())) {
      val operator = advance()
      val right = parseMultiplicative()
      expr = Expr.Binary(expr, operator, right)
    }
    return expr
  }

  private fun parseMultiplicative(): Expr {
    var expr = parseUnary()
    while (check(*MULTIPLICATIVE_OPERATORS.toTypedArray())) {
      val operator = advance()
      val right = parseUnary()
      expr = Expr.Binary(expr, operator, right)
    }
    return expr
  }

  private fun parseUnary(): Expr =
    if (check(*UNARY_TOKEN_TYPES.toTypedArray())) {
      val operator = advance()
      val right = parseUnary()
      Expr.Unary(operator, right)
    } else {
      parseCall()
    }

  private fun parseCall(): Expr {
    var expression = parsePrimary()
    while (check(TokenType.LEFT_PAREN)) {
      val paren = advance()
      val arguments =
        buildList {
          if (!check(TokenType.RIGHT_PAREN)) {
            add(parseExpression())
            while (check(TokenType.COMMA)) {
              advance()
              add(parseExpression())
            }
          }
        }
      consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")
      expression = Expr.Call(expression, paren, arguments)
    }
    return expression
  }

  private fun parsePrimary(): Expr {
    val token =
      peek()
        ?: throw ParseException("Unexpected end of input.", tokens.last())

    return when (token.type) {
      TokenType.FALSE -> {
        advance()
        Expr.Literal(false)
      }

      TokenType.TRUE -> {
        advance()
        Expr.Literal(true)
      }

      TokenType.NIL -> {
        advance()
        Expr.Literal(null)
      }

      TokenType.NUMBER -> {
        advance()
        Expr.Literal(token.lexeme.toDouble())
      }

      TokenType.STRING -> {
        advance()
        Expr.Literal(token.lexeme.removeSurrounding("\""))
      }

      TokenType.LEFT_PAREN -> {
        advance()
        val inner = parseExpression()
        consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.")
        Expr.Grouping(inner)
      }

      TokenType.IDENTIFIER -> {
        advance()
        Expr.Variable(token)
      }

      else -> throw ParseException("Expected expression, found '${token.lexeme}'.", token)
    }
  }

  private fun consume(
    type: TokenType,
    message: String,
  ): Token {
    if (check(type)) return advance()
    throw ParseException(message, peek()!!)
  }

  private fun check(vararg types: TokenType): Boolean = !isAtEnd() && types.any { peek()!!.type == it }

  private fun advance(): Token = tokens[currentIndex++]

  private fun peek(): Token? = tokens.getOrNull(currentIndex)

  private fun isAtEnd(): Boolean = peek()?.type == TokenType.EOF
}
