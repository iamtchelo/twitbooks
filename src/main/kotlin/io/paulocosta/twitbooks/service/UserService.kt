package io.paulocosta.twitbooks.service

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.entity.Friend import io.paulocosta.twitbooks.repository.FriendsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.TwitterProfile
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
        val twitterProvider: TwitterProvider,
        val friendsRepository: FriendsRepository) {

    /**
     * TODO: check how to iterate the cursor here
     **/
    fun syncUsers() {
        val friends = twitterProvider.getTwitter().friendOperations().friends
        saveFriends(friends)
    }

    fun hasUsers(): Boolean {
        return friendsRepository.count() > 0
    }

    private fun saveFriends(profiles: List<TwitterProfile>) {
        val friends = profiles.map { toFriend(it) }
        friendsRepository.saveAll(friends)
    }

    private fun toFriend(profile: TwitterProfile): Friend {
        return Friend(
                null,
                profile.id,
                profile.name,
                profile.screenName)
    }

}