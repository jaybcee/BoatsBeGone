package com.github.jaybcee.boatsbegone

import org.joda.time.DateTimeZone
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class BoatsbegoneApplication

fun main(args: Array<String>) {
    DateTimeZone.setDefault(DateTimeZone.forID("America/Toronto"))
    runApplication<BoatsbegoneApplication>(*args)
}
