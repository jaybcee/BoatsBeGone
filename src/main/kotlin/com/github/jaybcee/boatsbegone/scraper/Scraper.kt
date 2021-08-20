package com.github.jaybcee.boatsbegone.scraper

import com.github.jaybcee.boatsbegone.bot.FbBot
import com.github.jaybcee.boatsbegone.data.AppData
import org.joda.time.DateTime
import org.joda.time.Hours
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Scraper {

    @Autowired
    val appData: AppData = AppData()

    @Autowired
    val fbBot: FbBot = FbBot()

    var previousUpstreamStatus = "Available"

    /**
     * This annotation allows for scheduled execution of a task.
     * Unlike a Unix cron expression, this accepts 6 arguments.
     * This function will run every 2 minutes between 5am and 22:59pm everyday
     */
    @Scheduled(cron = "0 */2 5-22 * * *")
    fun checkLock() {
        val rows = scrape()
        val currentUpstreamStatus = rows.upstream.text
        if (previousUpstreamStatus == "Available" && currentUpstreamStatus.contains("Raison Soon")) {
            val message = "The bridge is RAISING SOON. Get going!"
            updateMessage(message)

        } else if (previousUpstreamStatus.contains("Unavailable") && currentUpstreamStatus.contains("Available")) {
            val message = "The bridge is now AVAILABLE. It has been available since %s:%s. Happy biking! 🚴"
            updateMessageWithTime(message)
        } else if (
            (previousUpstreamStatus.contains("Available") || previousUpstreamStatus.contains("Raising Soon"))
            && currentUpstreamStatus.contains("Unavailable")
        ) {
            appData.timeOfLastChange = DateTime.now()
            val message = "The bridge is UNAVAILABLE. It has been unavailable since %s:%s."
            updateMessageWithTime(message)
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
    fun cleanUpOldUsers() {
        val map = appData.activeUserMap
        map.keys.forEach {
            val deltaHours = Hours.hoursBetween(map[it], DateTime.now())
            if (deltaHours.hours >= 2) {
                map.remove(it)
                fbBot.sendMessageToUser(it, "Time is up! You have been removed after 2 hours.")
            }
        }
    }

    private fun updateMessage(text: String) {
        appData.currentMessage = text
        appData.timeOfLastChange = DateTime.now()
        alertUsers(text)
    }

    private fun updateMessageWithTime(text: String) {
        appData.timeOfLastChange = DateTime.now()
        val time = appData.timeOfLastChange
        val messageWithTime = String.format(text, time.hourOfDay, time.minuteOfHour)
        appData.currentMessage = messageWithTime
        alertUsers(text)
    }
}