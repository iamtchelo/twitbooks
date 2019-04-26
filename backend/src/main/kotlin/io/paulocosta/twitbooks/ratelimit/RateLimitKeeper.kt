package io.paulocosta.twitbooks.ratelimit

import io.paulocosta.twitbooks.entity.RateLimit

data class RateLimitKeeper(private val rateLimit: RateLimit) {

    private var hits = 0

    fun exceeded(): Boolean {
        return hits >= rateLimit.remainingHits
    }

    fun addHit() {
        hits += 1
    }

}
