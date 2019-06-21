package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.Friend
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FriendsRepository : JpaRepository<Friend, Long> {

    @Query("""
        select * from friends
        inner join user_friends
        on friends.id = user_friends.friend_id
        where user_friends.user_id = :userId""", nativeQuery = true)
    fun findByUserId(userId: Long): List<Friend>

}
