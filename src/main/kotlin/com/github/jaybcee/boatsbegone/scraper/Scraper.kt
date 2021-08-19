package com.github.jaybcee.boatsbegone.scraper

import com.github.jaybcee.boatsbegone.bot.FbBot
import com.github.jaybcee.boatsbegone.data.AppData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit
import java.util.Calendar

@Component
class Scraper {

    @Autowired
    val appData: AppData = AppData()

    @Autowired
    val fbBot: FbBot = FbBot()

    var previousUpstreamStatus = "Available"

    /**
     * This @Schedule annotation run every 5 seconds in this case. It can also
     * take a cron like syntax.
     * See https://
     * docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
     */
    @Scheduled(fixedRate = 5000)
    fun checkLock() {
        val rows = scrape()
        val currentUpstreamStatus = rows.upstream.findFirst("#status").text
        if (previousUpstreamStatus == "Unavailable" && currentUpstreamStatus == "Available") {
            val message = "The bridge is now AVAILABLE. Happy biking! 🚴"
            appData.currentMessage = message
            alertUsers(message)
        } else if (previousUpstreamStatus == "Available" && currentUpstreamStatus == "Unavailable") {
            val time = appData.timeOfLastChange.time
            val message = "The bridge is UNAVAILABLE. It has been unavailable since ${time.hours}:${time.minutes}."
            appData.currentMessage = message
            alertUsers(message)
        }
        previousUpstreamStatus = currentUpstreamStatus
        cleanUpOldUsers()
    }

    /**
     * Sends a message to all users.
     */
    fun alertUsers(text: String) {
        appData.activeUserMap.keys.forEach { userId ->
            fbBot.sendMessageToUser(userId, text)
        }
    }

    /**
     * Cleans up stale users
     */
    fun cleanUpOldUsers(){
        val map = appData.activeUserMap
        map.keys.forEach {
            val deltaHours = ChronoUnit.HOURS.between(map[it]!!.toInstant(),Calendar.getInstance().toInstant())
            if(deltaHours >= 2){
                map.remove(it)
                fbBot.sendMessageToUser(it, "Time is up! You have been removed after 2 hours.")
            }
        }
    }
}