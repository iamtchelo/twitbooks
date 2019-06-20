package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.entity.User
import io.paulocosta.twitbooks.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(private val repository: UserRepository) {

    fun findByTwitterId(id: String): User? {
        return repository.findByTwitterId(id)
    }

    fun getSyncableUsers(): List<User> {
        return repository.findSyncableUsers()
    }

    fun saveUser(user: User): User {
        return repository.save(user)
    }

}