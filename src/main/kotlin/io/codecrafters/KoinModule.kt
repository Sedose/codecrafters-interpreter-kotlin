package io.codecrafters

import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.component.impl.IdentifierProcessor
import io.codecrafters.tokenizer.component.impl.MultiCharTokenProcessor
import io.codecrafters.tokenizer.component.impl.NumberTokenProcessor
import io.codecrafters.tokenizer.component.impl.SingleCharTokenProcessor
import io.codecrafters.tokenizer.component.impl.SingleLineCommentSkipper
import io.codecrafters.tokenizer.component.impl.StringTokenProcessor
import org.koin.dsl.module

val appModule =
  module {
    single { SingleLineCommentSkipper() }
    single { StringTokenProcessor() }
    single { NumberTokenProcessor() }
    single { IdentifierProcessor() }
    single { SingleCharTokenProcessor() }
    single { MultiCharTokenProcessor() }

    single {
      listOf(
        get<SingleLineCommentSkipper>(),
        get<StringTokenProcessor>(),
        get<NumberTokenProcessor>(),
        get<IdentifierProcessor>(),
        get<SingleCharTokenProcessor>(),
        get<MultiCharTokenProcessor>(),
      )
    }

    single { Tokenizer(get()) }
  }
