package io.paulocosta.twitbooks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TwitbooksApplication

fun main(args: Array<String>) {
    runApplication<TwitbooksApplication>(*args)
}

