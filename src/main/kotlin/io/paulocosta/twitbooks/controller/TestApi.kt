package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.auth.TwitterProvider
import io.paulocosta.twitbooks.repository.FriendsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/test")
class TestApi {

    @Autowired
    lateinit var twitterProvider: TwitterProvider

    @Autowired
    lateinit var friendRepository: FriendsRepository

    @GetMapping("/1")
    fun testAuthenticity(): String {
        val template = twitterProvider.getTwitter()
        return "authenticated: ${template.isAuthorized}"
    }

    @GetMapping("/2")
    fun testRateLimit(): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        val template = twitterProvider.getTwitter()
        return template.userOperations().getRateLimitStatus(ResourceFamily.STATUSES)
    }

    @GetMapping("/3")
    fun testSearch(): SearchResults? {
        val twitter = twitterProvider.getTwitter()
        return twitter.searchOperations().search("#spring")
    }

    @GetMapping("/4")
    fun testGetFriendList(): CursoredList<TwitterProfile>? {
        val twitter = twitterProvider.getTwitter()
        return twitter.friendOperations().friends
    }

    @GetMapping("/5")
    fun testGetFriendsIds(): CursoredList<Long>? {
        val twitter = twitterProvider.getTwitter()
        return twitter.friendOperations().friendIds
    }

    @GetMapping("/6")
    fun testGetFriendTweets(): MutableList<Tweet>? {
        val twitter = twitterProvider.getTwitter()
        return twitter.timelineOperations().getUserTimeline(2420931980)
    }

    @GetMapping("/7")
    fun testDatabaseConnection(): String {
        val twitter = twitterProvider.getTwitter()
        val friendsIds = twitter.friendOperations().friendIds
        //friendRepository.saveAll(friendsIds.map { Friend(it) })
        return "ok"

    }

    @GetMapping("/8")
    fun testUserLookupByName(): MutableList<TwitterProfile>? {
        val twitter = twitterProvider.getTwitter()
        return twitter.userOperations().getUsers("jessitron")
    }

}