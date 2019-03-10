package io.paulocosta.twitbooks.extensions

import java.util.*

fun <T : Any> Optional<T>.toNullable(): T? = this.orElse(null)