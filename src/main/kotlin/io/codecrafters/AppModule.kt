package io.codecrafters

import io.codecrafters.command.CommandHandler
import io.codecrafters.command.EvaluateCommandHandler
import io.codecrafters.command.ParseCommandHandler
import io.codecrafters.command.RunCommandHandler
import io.codecrafters.command.TokenizeCommandHandler
import io.codecrafters.interpreter.Interpreter
import io.codecrafters.model.Command
import io.codecrafters.model.StderrSink
import io.codecrafters.model.StdoutSink
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

    single { StdoutSink() }
    single { StderrSink() }

    single { Tokenizer(get()) }
    single { AstStringifier() }
    single { Interpreter(get()) }

    single { TokenizeCommandHandler(get()) }
    single { ParseCommandHandler(get<AstStringifier>(), get()) }
    single { EvaluateCommandHandler(get<Interpreter>(), get()) }
    single { RunCommandHandler(get()) }

    single<Map<Command, CommandHandler>> {
      mapOf(
        Command.TOKENIZE to get<TokenizeCommandHandler>(),
        Command.PARSE to get<ParseCommandHandler>(),
        Command.EVALUATE to get<EvaluateCommandHandler>(),
        Command.RUN to get<RunCommandHandler>(),
      )
    }

    single { Application(get<Tokenizer>(), get<Map<Command, CommandHandler>>(), get()) }
  }
