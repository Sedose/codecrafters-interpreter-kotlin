import org.koin.core.component.KoinComponent

class Tokenizer : KoinComponent {
  private val tokenToType =
    mapOf(
      '(' to TokenType.LEFT_PAREN,
      ')' to TokenType.RIGHT_PAREN,
      '{' to TokenType.LEFT_BRACE,
      '}' to TokenType.RIGHT_BRACE,
      ',' to TokenType.COMMA,
      '.' to TokenType.DOT,
      '-' to TokenType.MINUS,
      '+' to TokenType.PLUS,
      ';' to TokenType.SEMICOLON,
      '*' to TokenType.STAR,
    )

  fun processToken(token: Char): String {
    val tokenType =
      tokenToType[token] ?: throw RuntimeException("Unknown token: $token")
    return "$tokenType $token null"
  }
}
