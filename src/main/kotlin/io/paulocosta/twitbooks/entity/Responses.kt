package io.paulocosta.twitbooks.entity

sealed class Response

data class MessageResult(val messages: List<Message>): Response()
