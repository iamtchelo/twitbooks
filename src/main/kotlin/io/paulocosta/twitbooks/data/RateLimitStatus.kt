package io.paulocosta.twitbooks.data

sealed class RateLimitStatus

object Exceeded : RateLimitStatus()

object Allowed : RateLimitStatus()
