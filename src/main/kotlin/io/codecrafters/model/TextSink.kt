package io.codecrafters.model

fun interface TextSink {
  fun write(line: String)
}

class StdoutSink : TextSink {
  override fun write(line: String) = println(line)
}

class StderrSink : TextSink {
  override fun write(line: String) = System.err.println(line)
}
