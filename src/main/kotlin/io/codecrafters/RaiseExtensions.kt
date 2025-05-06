package io.codecrafters

import arrow.core.raise.ExperimentalTraceApi
import arrow.core.raise.Raise
import arrow.core.raise.Trace
import arrow.core.raise.traced
import arrow.core.raise.withError
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalTraceApi::class, ExperimentalContracts::class)
inline fun <Error, OtherError, A> Raise<Error>.withErrorTraced(
  transform: (Trace, OtherError) -> Error,
  block: Raise<OtherError>.() -> A
): A {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  withError({ _: OtherError -> error("should never be called") }) {
    traced({ return block(this) }) { trace, error -> raise(transform(trace, error)) }
  }
}
