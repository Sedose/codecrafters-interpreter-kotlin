package io.codecrafters

import io.codecrafters.tokenizer.Tokenizer
import org.koin.dsl.module

val appModule =
    module {
        single { Tokenizer() }
    }
