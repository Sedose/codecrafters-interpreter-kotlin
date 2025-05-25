package io.codecrafters

import io.codecrafters.application.command.*
import io.codecrafters.model.Command
import io.codecrafters.tokenizer.Tokenizer
import io.codecrafters.tokenizer.component.impl.IdentifierProcessor
import io.codecrafters.tokenizer.component.impl.MultiCharTokenProcessor
import io.codecrafters.tokenizer.component.impl.NumberTokenProcessor
import io.codecrafters.tokenizer.component.impl.SingleCharTokenProcessor
import io.codecrafters.tokenizer.component.impl.SingleLineCommentSkipper
import io.codecrafters.tokenizer.component.impl.StringTokenProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config {
  @Bean
  fun commandHandlers(
    tokenizeHandler: TokenizeCommandHandler,
    parseHandler: ParseCommandHandler,
    evaluateHandler: EvaluateCommandHandler,
    runHandler: RunCommandHandler,
  ): Map<Command, CommandHandler> =
    mapOf(
      Command.TOKENIZE to tokenizeHandler,
      Command.PARSE to parseHandler,
      Command.EVALUATE to evaluateHandler,
      Command.RUN to runHandler,
    )

  @Bean
  fun tokenizer(
    identifierProcessor: IdentifierProcessor,
    multiCharTokenProcessor: MultiCharTokenProcessor,
    numberTokenProcessor: NumberTokenProcessor,
    singleCharTokenProcessor: SingleCharTokenProcessor,
    singleLineCommentSkipper: SingleLineCommentSkipper,
    stringTokenProcessor: StringTokenProcessor,
  ): Tokenizer =
    listOf(
      singleLineCommentSkipper,
      stringTokenProcessor,
      numberTokenProcessor,
      identifierProcessor,
      multiCharTokenProcessor,
      singleCharTokenProcessor,
    ).let(::Tokenizer)
}
