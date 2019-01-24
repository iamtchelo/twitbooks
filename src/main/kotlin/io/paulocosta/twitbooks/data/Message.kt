package io.paulocosta.twitbooks.data

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class Message(
        @Id val id: Long,
        val text: String,
        @ManyToOne
        @JoinColumn(name = "friend_id")
        val friend: Friend)