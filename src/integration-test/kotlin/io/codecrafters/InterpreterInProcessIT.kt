package io.codecrafters

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.File

data class EvaluateTestCase(
  val resourcePath: String,
  val expectedOutput: String,
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InterpreterInProcessIT : KoinTest {
  private val application: Application by inject()

  @BeforeEach
  fun setup() {
    stopKoin()
    startKoin { modules(appModule) }
  }

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
