package io.codecrafters

import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(appModule)
    }

    TokenizerApp().run(args)
}
