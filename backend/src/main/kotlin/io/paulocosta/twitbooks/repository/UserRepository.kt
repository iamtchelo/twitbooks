package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, String> {

    @Query("select user from User user where user.accessToken is not null and user.accessTokenSecret is not null")
    fun findSyncableUsers(): List<User>

    fun findByTwitterId(twitterId: String): User?

}