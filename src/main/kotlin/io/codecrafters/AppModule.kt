package io.codecrafters

import io.codecrafters.interpreter.Interpreter
import io.codecrafters.parser.AstStringifier
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
        get<MultiCharTokenProcessor>(),
        get<SingleCharTokenProcessor>(),
      )
    }

    single { Tokenizer(get()) }
    single { AstStringifier() }
    single { Interpreter() }
    single { Application(get(), get(), get()) }
  }
