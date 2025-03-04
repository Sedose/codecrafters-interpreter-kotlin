package io.codecrafters.tokenizer.component

class SingleLineCommentSkipper {
  fun skipSingleLineComment(
    input: String,
    currentIndex: Int,
  ): Int =
    input
      .indexOf('\n', currentIndex)
      .takeUnless { it == -1 }
      ?: input.length
}
