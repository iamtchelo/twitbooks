package io.paulocosta.twitbooks.api

import io.paulocosta.twitbooks.auth.TwitterAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.RateLimitStatus
import org.springframework.social.twitter.api.ResourceFamily
import org.springframework.social.twitter.api.SearchResults
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/test")
class TestApi {

    @Autowired
    lateinit var twitterAuth: TwitterAuth

    @GetMapping("/1")
    fun testAuthenticity(): String {
        val template = twitterAuth.getTwitter()
        return "authenticated: ${template.isAuthorized}"
    }

    @GetMapping("/2")
    fun testRateLimit(): MutableMap<ResourceFamily, MutableList<RateLimitStatus>>? {
        val template = twitterAuth.getTwitter()
        return template.userOperations().getRateLimitStatus(ResourceFamily.SEARCH)
    }

    @GetMapping("/3")
    fun testSearch(): SearchResults? {
        val twitter = twitterAuth.getTwitter()
        return twitter.searchOperations().search("#spring")
    }

}