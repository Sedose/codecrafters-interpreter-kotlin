package io.codecrafters

import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.component.impl.IdentifierProcessor
import io.codecrafters.tokenizer.component.impl.MultiCharTokenProcessor
import io.codecrafters.tokenizer.component.impl.NumberTokenProcessor
import io.codecrafters.tokenizer.component.impl.SingleCharTokenProcessor
import io.codecrafters.tokenizer.component.impl.SingleLineCommentSkipper
import io.codecrafters.tokenizer.component.impl.StringTokenProcessor
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main(args: Array<String>) {
  startKoin {
    modules(appModule)
  }.run {
    koin.get<Application>().run(args)
  }
}

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
//    single { AstPrinter() }
//    single { Application(get(), get()) }
    single { Application(get()) }
  }
