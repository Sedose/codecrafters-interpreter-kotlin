package io.codecrafters

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

data class ParseTestCase(
  val resourcePath: String,
  val expectedOutput: String,
  val expectedExitCode: Int = 0,
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParserInProcessIT : KoinTest {
  private val application: Application by inject()

  @BeforeEach
  fun setup() {
    stopKoin()
    startKoin { modules(appModule) }
  }

  companion object {
    @JvmStatic
    fun parseTestCases() =
      listOf(
        ParseTestCase("src/integration-test/resources/literal_true.lox", "true"),
        ParseTestCase("src/integration-test/resources/literal_false.lox", "false"),
        ParseTestCase("src/integration-test/resources/literal_nil.lox", "nil"),
        ParseTestCase("src/integration-test/resources/literal_number.lox", "42.47"),
        ParseTestCase("src/integration-test/resources/literal_string.lox", "hello"),
        ParseTestCase("src/integration-test/resources/grouping_expression.lox", "(group 42.47)"),
        ParseTestCase("src/integration-test/resources/grouping_string.lox", "(group hello)"),
        ParseTestCase("src/integration-test/resources/unary_negation.lox", "(- 42.0)"),
        ParseTestCase("src/integration-test/resources/unary_not.lox", "(! true)"),
        ParseTestCase("src/integration-test/resources/multiplication_test.lox", "(* 16.0 38.0)"),
        ParseTestCase("src/integration-test/resources/division_test.lox", "(/ 38.0 58.0)"),
        ParseTestCase("src/integration-test/resources/combined_multiplication_division_test.lox", "(/ (* 16.0 38.0) 58.0)"),
        ParseTestCase("src/integration-test/resources/addition_subtraction_operators.lox", "(- (+ 52.0 80.0) 94.0)"),
        ParseTestCase("src/integration-test/resources/comparison_less.lox", "(< 5.0 10.0)"),
        ParseTestCase("src/integration-test/resources/comparison_less_equal.lox", "(<= 8.0 9.0)"),
        ParseTestCase("src/integration-test/resources/comparison_greater.lox", "(> 11.0 7.0)"),
        ParseTestCase("src/integration-test/resources/comparison_greater_equal.lox", "(>= 15.0 15.0)"),
        ParseTestCase("src/integration-test/resources/chain_comparisons.lox", "(< (< 1.0 2.0) 3.0)"),
        ParseTestCase("src/integration-test/resources/equality_equal.lox", "(== baz baz)"),
        ParseTestCase("src/integration-test/resources/equality_not_equal.lox", "(!= foo bar)"),
        ParseTestCase("src/integration-test/resources/chained_equality.lox", "(== (!= foo bar) baz)"),
      )
  }

  @ParameterizedTest
  @MethodSource("parseTestCases")
  fun `parse literals and print AST - in process`(testCase: ParseTestCase) {
    val (resourcePath, expectedOutput) = testCase

    val stdout = ByteArrayOutputStream()
    val originalOut = System.out
    System.setOut(PrintStream(stdout))

    try {
      application.run(arrayOf("parse", File(resourcePath).absolutePath))
    } finally {
      System.setOut(originalOut)
    }

    val output = stdout.toString().trim()
    assert(output == expectedOutput) {
      "Expected: $expectedOutput\nActual: $output"
    }
  }
}
