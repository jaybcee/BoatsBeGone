package com.github.jaybcee.boatsbegone

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class BoatsbegoneApplication

fun main(args: Array<String>) {
    runApplication<BoatsbegoneApplication>(*args)
}
