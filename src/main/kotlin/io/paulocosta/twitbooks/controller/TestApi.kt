package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.auth.TwitterAuth
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
    lateinit var twitterAuth: TwitterAuth

    @Autowired
    lateinit var friendRepository: FriendsRepository

    @GetMapping("/1")
    fun testAuthenticity(): String {
        val template = twitterAuth.getTwitter()
        return "authenticated: ${template.isAuthorized}"
    }

    @GetMapping("/2")
    fun testRateLimit(): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        val template = twitterAuth.getTwitter()
        return template.userOperations().getRateLimitStatus(ResourceFamily.STATUSES)
    }

    @GetMapping("/3")
    fun testSearch(): SearchResults? {
        val twitter = twitterAuth.getTwitter()
        return twitter.searchOperations().search("#spring")
    }

    @GetMapping("/4")
    fun testGetFriendList(): CursoredList<TwitterProfile>? {
        val twitter = twitterAuth.getTwitter()
        return twitter.friendOperations().friends
    }

    @GetMapping("/5")
    fun testGetFriendsIds(): CursoredList<Long>? {
        val twitter = twitterAuth.getTwitter()
        return twitter.friendOperations().friendIds
    }

    @GetMapping("/6")
    fun testGetFriendTweets(): MutableList<Tweet>? {
        val twitter = twitterAuth.getTwitter()
        return twitter.timelineOperations().getUserTimeline(2420931980)
    }

    @GetMapping("/7")
    fun testDatabaseConnection(): String {
        val twitter = twitterAuth.getTwitter()
        val friendsIds = twitter.friendOperations().friendIds
        //friendRepository.saveAll(friendsIds.map { Friend(it) })
        return "ok"

    }

    @GetMapping("/8")
    fun testUserLookupByName(): MutableList<TwitterProfile>? {
        val twitter = twitterAuth.getTwitter()
        return twitter.userOperations().getUsers("jessitron")
    }

}