package com.github.jaybcee.boatsbegone.scraper

import com.github.jaybcee.boatsbegone.bot.FbBot
import com.github.jaybcee.boatsbegone.data.AppData
import com.github.jaybcee.boatsbegone.status.Statuses.AVAILABLE_MESSAGE
import com.github.jaybcee.boatsbegone.status.Statuses.EXPIRED_TIME
import com.github.jaybcee.boatsbegone.status.Statuses.HOURS_AND_MINUTES
import com.github.jaybcee.boatsbegone.status.Statuses.UNAVAILABLE_MESSAGE
import com.github.jaybcee.boatsbegone.status.Statuses.UNAVAILABLE_SOON
import org.joda.time.DateTime
import org.joda.time.Hours
import org.joda.time.Hours.hoursBetween
import org.joda.time.Minutes
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
        if (previousUpstreamStatus == "Available" && currentUpstreamStatus.contains("Raising Soon")) {
            updateMessage(UNAVAILABLE_SOON)
        } else if (previousUpstreamStatus.contains("Unavailable") && currentUpstreamStatus.contains("Available")) {
            updateMessageWithTime(AVAILABLE_MESSAGE)
        } else if (
            (previousUpstreamStatus.contains("Available") || previousUpstreamStatus.contains("Raising Soon")) &&
            currentUpstreamStatus.contains("Unavailable")
        ) {
            appData.timeOfLastChange = DateTime.now()
            updateMessageWithTime(UNAVAILABLE_MESSAGE)
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
            val deltaMinutes = Minutes.minutesBetween(map[it], DateTime.now())
            if (deltaMinutes.minutes >= 120) {
                map.remove(it)
                fbBot.sendMessageToUser(it, EXPIRED_TIME)
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
        val messageWithTime = String.format(text, appData.timeOfLastChange.toString(HOURS_AND_MINUTES))
        appData.currentMessage = messageWithTime
        alertUsers(messageWithTime)
    }
}
