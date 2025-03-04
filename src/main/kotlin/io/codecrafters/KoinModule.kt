package io.codecrafters

import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.component.IdentifierProcessor
import io.codecrafters.tokenizer.component.NumberTokenProcessor
import io.codecrafters.tokenizer.component.SingleLineCommentSkipper
import io.codecrafters.tokenizer.component.StringTokenProcessor
import org.koin.dsl.module

val appModule =
  module {
    single { SingleLineCommentSkipper() }
    single { StringTokenProcessor() }
    single { NumberTokenProcessor() }
    single { IdentifierProcessor() }
    single { Tokenizer(get(), get(), get(), get()) }
  }
