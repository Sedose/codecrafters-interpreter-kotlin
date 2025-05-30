package io.codecrafters

import io.codecrafters.application.Application
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

data class EvaluateTestCase(
  val resourcePath: String,
  val expectedOutput: String,
)

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InterpreterInProcessIT {
  @Autowired
  private lateinit var application: Application

  companion object {
    @JvmStatic
    fun evaluateTestCases() =
      listOf(
        EvaluateTestCase("src/integration-test/resources/literal_true.lox", "true"),
        EvaluateTestCase("src/integration-test/resources/literal_false.lox", "false"),
        EvaluateTestCase("src/integration-test/resources/literal_nil.lox", "nil"),
        EvaluateTestCase("src/integration-test/resources/literal_string.lox", "hello"),
        EvaluateTestCase("src/integration-test/resources/number_integer_test.lox", "42"),
        EvaluateTestCase("src/integration-test/resources/literal_number.lox", "42.47"),
        EvaluateTestCase("src/integration-test/resources/unary_negation.lox", "-42"),
        EvaluateTestCase("src/integration-test/resources/unary_not.lox", "false"),
        EvaluateTestCase("src/integration-test/resources/multiplication_test.lox", "608"),
        EvaluateTestCase("src/integration-test/resources/division_test.lox", "0.6551724137931034"),
        EvaluateTestCase("src/integration-test/resources/combined_multiplication_division_test.lox", "10.482758620689655"),
        EvaluateTestCase("src/integration-test/resources/addition_subtraction_operators.lox", "38"),
        EvaluateTestCase("src/integration-test/resources/string_concat.lox", "hello world!"),
        EvaluateTestCase("src/integration-test/resources/comparison_greater.lox", "true"),
        EvaluateTestCase("src/integration-test/resources/comparison_greater_equal.lox", "true"),
        EvaluateTestCase("src/integration-test/resources/comparison_less.lox", "true"),
        EvaluateTestCase("src/integration-test/resources/comparison_less_equal.lox", "true"),
        EvaluateTestCase("src/integration-test/resources/comparison_greater_false.lox", "false"),
        EvaluateTestCase("src/integration-test/resources/comparison_greater_equal_edge.lox", "false"),
        EvaluateTestCase("src/integration-test/resources/comparison_decimal.lox", "true"),
        EvaluateTestCase("src/integration-test/resources/comparison_less_equal_edge.lox", "true"),
        EvaluateTestCase("src/integration-test/resources/equality_equal.lox", "true"),
        EvaluateTestCase("src/integration-test/resources/equality_not_equal.lox", "true"),
      )
  }

  @ParameterizedTest
  @MethodSource("evaluateTestCases")
  fun `evaluate expressions and print result - in process`(testCase: EvaluateTestCase) {
    val (resourcePath, expectedOutput) = testCase
    val output = application.runAndCaptureOutput(arrayOf("evaluate", File(resourcePath).absolutePath))
    assert(output == expectedOutput) {
      "Expected: $expectedOutput\nActual: $output"
    }
  }
}
