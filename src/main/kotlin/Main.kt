import org.koin.core.context.startKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.system.exitProcess

class TokenizerApp : KoinComponent {
  private val tokenizer: Tokenizer by inject()

  fun run(args: Array<String>) {
    System.err.println("Logs from your program will appear here!")

    if (args.size < 2) {
      System.err.println("Usage: ./your_program.sh tokenize <filename>")
      exitProcess(1)
    }

    val (command, filename) = args
    if (command != "tokenize") {
      System.err.println("Unknown command: $command")
      exitProcess(1)
    }

    val fileContents = File(filename).readText()
    if (fileContents.isNotEmpty()) {
      fileContents.toCharArray()
        .joinToString(separator = "\n") { tokenizer.processToken(it) }
        .let(::println)
    }
    println("EOF  null")
  }
}

fun main(args: Array<String>) {
  startKoin {
    modules(appModule)
  }

  TokenizerApp().run(args)
}
