package io.codecrafters.tokenizer.component.impl

import io.codecrafters.tokenizer.component.impl.model.CanProcessCase
import io.codecrafters.tokenizer.component.impl.model.SkipProcessingCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(Lifecycle.PER_CLASS)
class SingleLineCommentSkipperTest {
  private val processor = SingleLineCommentSkipper()

  @ParameterizedTest
  @MethodSource("canProcessDataProvider")
  fun testCanProcess(testCase: CanProcessCase) {
    val result = processor.canProcess(testCase.input, testCase.startIndex)
    assertEquals(testCase.expectCanProcess, result)
  }

  @Suppress("UnusedPrivateMember")
  private fun canProcessDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of("Double slash at start", CanProcessCase("// comment", 0, true)),
      ),
      Arguments.of(
        Named.of("Double slash in middle", CanProcessCase("abc//def", 3, true)),
      ),
      Arguments.of(
        Named.of("Single slash", CanProcessCase("/notcomment", 0, false)),
      ),
      Arguments.of(
        Named.of("Incomplete slash at end", CanProcessCase("a/", 1, false)),
      ),
      Arguments.of(
        Named.of("Index out of bounds", CanProcessCase("//", 1, false)),
      ),
      Arguments.of(
        Named.of("Negative index", CanProcessCase("//", -1, false)),
      ),
      Arguments.of(
        Named.of("Non-comment chars", CanProcessCase("xyz", 0, false)),
      ),
    )

  @ParameterizedTest
  @MethodSource("processDataProvider")
  fun testProcess(testCase: SkipProcessingCase) {
    val result = processor.process(testCase.input, testCase.startIndex, 1)
    assertNull(result.token)
    assertEquals(testCase.expectedNewIndex, result.newIndex)
    assertNull(result.error)
  }

  @Suppress("UnusedPrivateMember")
  private fun processDataProvider(): Stream<Arguments> =
    Stream.of(
      Arguments.of(
        Named.of(
          "Skip till newline",
          SkipProcessingCase(
            input = "// comment\nnextLine",
            startIndex = 0,
            expectedNewIndex = 10,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Skip till end of input",
          SkipProcessingCase(
            input = "// comment without newline",
            startIndex = 0,
            expectedNewIndex = 26,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Comment in middle of input",
          SkipProcessingCase(
            input = "abc//def\nghi",
            startIndex = 3,
            expectedNewIndex = 8,
          ),
        ),
      ),
      Arguments.of(
        Named.of(
          "Windows line ending",
          SkipProcessingCase(
            input = "// comment\r\nnextLine",
            startIndex = 0,
            expectedNewIndex = 11,
          ),
        ),
      ),
    )
}
