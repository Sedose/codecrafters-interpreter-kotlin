package io.codecrafters

import org.koin.dsl.module

val appModule =
    module {
        single { Tokenizer() }
    }
