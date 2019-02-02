package io.paulocosta.twitbooks.entity

import java.util.*

data class RateLimit(
        val quarterOfHourLimit: Int,
        val remainingHits: Int,
        val resetTimeInSeconds: Long,
        val resetTime: Date) {

    fun exceeded(): Boolean {
        return remainingHits == 0
    }

}

