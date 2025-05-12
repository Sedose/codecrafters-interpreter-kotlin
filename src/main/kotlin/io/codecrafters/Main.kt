package io.codecrafters

import io.codecrafters.application.Application
import io.codecrafters.model.error.InterpreterException
import io.codecrafters.model.error.NotEnoughCliArgsException
import io.codecrafters.model.error.ParseException
import io.codecrafters.model.error.TokenizationErrorsDetectedException
import io.codecrafters.model.error.UnknownCommandException
import org.koin.core.context.startKoin
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  startKoin {
    modules(appModule)
  }.run {
    val application = koin.get<Application>()
    try {
      application.run(args)
    } catch (e: InterpreterException) {
      System.err.println(e.message)
      System.err.println("[line ${e.lineNumber}]")
      System.err.println("Trace: ${e.stackTraceToString()}")
      exitProcess(70)
    } catch (e: ParseException) {
      System.err.println("[line ${e.token.lineNumber}] Error at '${e.token.lexeme}': ${e.message}")
      exitProcess(65)
    } catch (_: NotEnoughCliArgsException) {
      System.err.println("Usage: ./your_program.sh <command> <filename>")
      exitProcess(1)
    } catch (e: UnknownCommandException) {
      System.err.println(e.message)
      exitProcess(1)
    } catch (_: TokenizationErrorsDetectedException) {
      exitProcess(65)
    }
  }
}
