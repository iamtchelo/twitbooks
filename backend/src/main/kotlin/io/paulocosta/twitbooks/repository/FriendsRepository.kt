package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Friend
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FriendsRepository : JpaRepository<Friend, Long>
