package io.paulocosta.twitbooks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TwitbooksApplication

fun main(args: Array<String>) {
    runApplication<TwitbooksApplication>(*args)
}

