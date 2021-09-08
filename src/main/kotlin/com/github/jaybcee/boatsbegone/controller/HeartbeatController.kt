package com.github.jaybcee.boatsbegone.controller

import org.joda.time.DateTime
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HeartbeatController {
    /**
     * Used to verify is server is running.
     */
    @GetMapping("/ping")
    fun pong(): String {
        return "pong ${DateTime.now()}"
    }
}
