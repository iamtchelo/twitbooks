package io.paulocosta.twitbooks.repository

import io.paulocosta.twitbooks.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>