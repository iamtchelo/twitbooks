package io.paulocosta.twitbooks.controller

import io.paulocosta.twitbooks.nerclient.NERApiService
import io.paulocosta.twitbooks.service.MessageService
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class Status(val nerApiStatus: String, val messageCount: Int)

@RestController
@RequestMapping("/status")
@CrossOrigin("*")
class StatusController @Autowired constructor(
        val messageService: MessageService,
        val nerApiService: NERApiService) {

    @GetMapping
    fun getStatus(): Single<Status> {
        val messageCount = messageService.getCount()
        return nerApiService.status()
                .toSingle { Status("ONLINE", messageCount) }
                .onErrorReturn { Status("OFFLINE", messageCount) }
    }

}
