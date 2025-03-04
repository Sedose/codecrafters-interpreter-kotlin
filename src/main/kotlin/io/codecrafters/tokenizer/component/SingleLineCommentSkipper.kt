package io.codecrafters.tokenizer.component

import org.koin.core.component.KoinComponent

class SingleLineCommentSkipper : KoinComponent {
  fun skipSingleLineComment(
    input: String,
    currentIndex: Int,
  ): Int =
    input
      .indexOf('\n', currentIndex)
      .takeUnless { it == -1 }
      ?: input.length
}
