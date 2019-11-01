package io.paulocosta.twitbooks.auth

import io.paulocosta.twitbooks.entity.User

interface UserInfoProvider {
    fun getUser(): User
}
