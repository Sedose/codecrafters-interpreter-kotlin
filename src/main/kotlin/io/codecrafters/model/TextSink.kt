package io.codecrafters.model

import org.springframework.stereotype.Component

fun interface TextSink {
  fun write(line: String)
}

@Component
class StdoutSink : TextSink {
  override fun write(line: String) = println(line)
}

@Component
class StderrSink : TextSink {
  override fun write(line: String) = System.err.println(line)
}
