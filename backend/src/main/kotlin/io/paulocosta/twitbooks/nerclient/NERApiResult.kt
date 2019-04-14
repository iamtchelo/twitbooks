package io.paulocosta.twitbooks.nerclient

data class NERApiResult(val entities: List<String>) {
    constructor(): this(emptyList())
}
