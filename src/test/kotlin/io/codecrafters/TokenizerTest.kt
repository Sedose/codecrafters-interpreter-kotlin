package io.codecrafters

import io.codecrafters.model.IdentifierProcessingResult
import io.codecrafters.model.ProcessingResult
import io.codecrafters.model.Token
import io.codecrafters.model.TokenType
import io.codecrafters.model.TokenizationResult
import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.component.IdentifierProcessor
import io.codecrafters.tokenizer.component.MultiCharTokenProcessor
import io.codecrafters.tokenizer.component.NumberTokenProcessor
import io.codecrafters.tokenizer.component.SingleCharTokenProcessor
import io.codecrafters.tokenizer.component.SingleLineCommentSkipper
import io.codecrafters.tokenizer.component.StringTokenProcessor
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class TokenizerTest {
  private val commentSkipper: SingleLineCommentSkipper = mockk()
  private val stringProcessor: StringTokenProcessor = mockk()
  private val numberProcessor: NumberTokenProcessor = mockk()
  private val identifierProcessor: IdentifierProcessor = mockk()
  private val singleCharProcessor: SingleCharTokenProcessor = mockk()
  private val multiCharProcessor: MultiCharTokenProcessor = mockk()

  private lateinit var tokenizer: Tokenizer

  @BeforeEach
  fun setup() {
    tokenizer =
      Tokenizer(
        commentSkipper,
        stringProcessor,
        numberProcessor,
        identifierProcessor,
        singleCharProcessor,
        multiCharProcessor,
      )
  }

  data class TestCase(
    val name: String,
    val input: String,
    val mockSetup: () -> Unit,
    val expectedTokens: List<Token>,
    val expectedErrors: List<String>,
  )

  @TestFactory
  fun comprehensiveTests(): List<DynamicTest> {
    val cases =
      listOf(
        TestCase(
          name = "Whitespace only",
          input = "   ",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
          },
          expectedTokens = emptyList(),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "New line increments line number",
          input = "\n",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
          },
          expectedTokens = emptyList(),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "Single-line comment",
          input = "// Hello world\n",
          mockSetup = {
            every { commentSkipper.skipSingleLineComment(any(), any()) } answers { (firstArg<String>()).length }
            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
          },
          expectedTokens = emptyList(),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "Multi-char token recognized",
          input = "==",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken('=') } returns true
            every { multiCharProcessor.process('=', '=') } returns Token(TokenType.EQUAL_EQUAL, "==")
            every { singleCharProcessor.canProcess(any()) } returns false
          },
          expectedTokens = listOf(Token(TokenType.EQUAL_EQUAL, "==")),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "Single-char token recognized",
          input = "+",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken('+') } returns false
            every { singleCharProcessor.canProcess('+') } returns true
            every { singleCharProcessor.process('+') } returns Token(TokenType.PLUS, "+")
          },
          expectedTokens = listOf(Token(TokenType.PLUS, "+")),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "String token success",
          input = "\"abc\"",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
            every { stringProcessor.processString("\"abc\"", 0, 1) } returns
              ProcessingResult(
                token = Token(TokenType.STRING, "\"abc\"", "abc"),
                newIndex = 5,
                error = null,
              )
          },
          expectedTokens = listOf(Token(TokenType.STRING, "\"abc\"", "abc")),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "String token unterminated",
          input = "\"abc\n",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
            every { stringProcessor.processString("\"abc\n", 0, 1) } returns
              ProcessingResult(
                token = null,
                newIndex = 4,
                error = "[line 1] Error: Unterminated string.",
              )
          },
          expectedTokens = emptyList(),
          expectedErrors = listOf("[line 1] Error: Unterminated string."),
        ),
        TestCase(
          name = "Number token success",
          input = "123",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
            every { numberProcessor.processNumber("123", 0, 1) } returns
              io.codecrafters.model.ProcessingResult(
                token = Token(TokenType.NUMBER, "123", 123.0),
                newIndex = 3,
                error = null,
              )
          },
          expectedTokens = listOf(Token(TokenType.NUMBER, "123", 123.0)),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "Number token with double dot error",
          input = "123.45.67",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
            every { numberProcessor.processNumber("123.45.67", 0, 1) } returns
              ProcessingResult(
                token = null,
                newIndex = 9,
                error = "[line 1] Error: Unexpected character: .",
              )
          },
          expectedTokens = emptyList(),
          expectedErrors = listOf("[line 1] Error: Unexpected character: ."),
        ),
        TestCase(
          name = "Identifier token success",
          input = "abc123",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
            every { identifierProcessor.processIdentifierOrKeyword("abc123", 0) } returns
              IdentifierProcessingResult(
                token = Token(TokenType.IDENTIFIER, "abc123"),
                newIndex = 6,
              )
          },
          expectedTokens = listOf(Token(TokenType.IDENTIFIER, "abc123")),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "Unknown character error",
          input = "#",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken('#') } returns false
            every { singleCharProcessor.canProcess('#') } returns false
          },
          expectedTokens = emptyList(),
          expectedErrors = listOf("[line 1] Error: Unexpected character: #"),
        ),
        TestCase(
          name = "Slash not comment",
          input = "/+",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken('/') } returns false
            every { multiCharProcessor.process(any(), any()) } returns null
            every { singleCharProcessor.canProcess('/') } returns true
            every { singleCharProcessor.process('/') } returns Token(TokenType.SLASH, "/")

            // For '+', we either skip or produce a plus token if we want to see 2 tokens in total.
            every { multiCharProcessor.isMultiCharToken('+') } returns false
            every { singleCharProcessor.canProcess('+') } returns true
            every { singleCharProcessor.process('+') } returns Token(TokenType.PLUS, "+")
          },
          expectedTokens =
            listOf(
              Token(TokenType.SLASH, "/"),
              Token(TokenType.PLUS, "+"),
            ),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "Multi-char token returns null",
          input = "!x",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken('!') } returns true
            every { multiCharProcessor.process('!', 'x') } returns null
            every { singleCharProcessor.canProcess('!') } returns true
            every { singleCharProcessor.process('!') } returns Token(TokenType.BANG, "!")
            every { multiCharProcessor.isMultiCharToken('x') } returns false
            every { singleCharProcessor.canProcess('x') } returns false
            every { stringProcessor.processString(any(), any(), any()) } returns ProcessingResult(null, 0, null)
            every { numberProcessor.processNumber(any(), any(), any()) } returns ProcessingResult(null, 0, null)
            every {
              identifierProcessor.processIdentifierOrKeyword("!x", 1)
            } returns
              IdentifierProcessingResult(
                Token(TokenType.IDENTIFIER, "x"),
                2,
              )
          },
          expectedTokens =
            listOf(
              Token(TokenType.BANG, "!"),
              Token(TokenType.IDENTIFIER, "x"),
            ),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "Underscore as identifier",
          input = "_abc",
          mockSetup = {
            every { multiCharProcessor.isMultiCharToken('_') } returns false
            every { singleCharProcessor.canProcess('_') } returns false
            every { stringProcessor.processString(any(), any(), any()) } returns ProcessingResult(null, 0, null)
            every { numberProcessor.processNumber(any(), any(), any()) } returns ProcessingResult(null, 0, null)
            every {
              identifierProcessor.processIdentifierOrKeyword("_abc", 0)
            } returns
              IdentifierProcessingResult(
                Token(TokenType.IDENTIFIER, "_abc"),
                4,
              )
          },
          expectedTokens =
            listOf(
              Token(TokenType.IDENTIFIER, "_abc"),
            ),
          expectedErrors = emptyList(),
        ),
        TestCase(
          name = "Single-line comment with no newline at the end",
          input = "// No newline here",
          mockSetup = {
            every {
              commentSkipper.skipSingleLineComment("// No newline here", 0)
            } returns "// No newline here".length

            every { multiCharProcessor.isMultiCharToken(any()) } returns false
            every { singleCharProcessor.canProcess(any()) } returns false
          },
          expectedTokens = emptyList(),
          expectedErrors = emptyList(),
        ),
      )

    return cases.map { testCase ->
      DynamicTest.dynamicTest(testCase.name) {
        testCase.mockSetup()
        val result: TokenizationResult = tokenizer.tokenize(testCase.input)
        assertEquals(testCase.expectedTokens, result.tokens)
        assertEquals(testCase.expectedErrors, result.errors)
      }
    }
  }
}
