package com.github.jaybcee.boatsbegone.bot

import com.github.jaybcee.boatsbegone.data.AppData
import com.github.jaybcee.boatsbegone.status.Statuses.SESSION_EXISTS
import com.github.jaybcee.boatsbegone.status.Statuses.START_MESSAGE
import com.github.jaybcee.boatsbegone.status.Statuses.STOP_MESSAGE
import com.github.jaybcee.boatsbegone.status.Statuses.UNKNOWN_MESSAGE
import me.ramswaroop.jbot.core.common.Controller
import me.ramswaroop.jbot.core.common.EventType
import me.ramswaroop.jbot.core.common.JBot
import me.ramswaroop.jbot.core.facebook.Bot
import me.ramswaroop.jbot.core.facebook.models.Event
import me.ramswaroop.jbot.core.facebook.models.User
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile

@JBot
@Profile("facebook")
class FbBot : Bot() {

    @Autowired
    val appData: AppData = AppData()

    /**
     * Set this property in `application.properties`.
     */
    @Value("\${fbBotToken}")
    private val fbToken: String? = null

    override fun getFbToken(): String {
        return fbToken!!
    }

    /**
     * Set this property in `application.properties`.
     */
    @Value("\${fbPageAccessToken}")
    private val pageAccessToken: String? = null

    override fun getPageAccessToken(): String {
        return pageAccessToken!!
    }

    @Controller(events = [EventType.MESSAGE, EventType.QUICK_REPLY], pattern = "(?i)status.*")
    fun onAskForStatus(event: Event) {
        reply(event, QuickMessage(appData.currentMessage))
    }

    /**
     * Registers user such that they receive notifications.
     */
    @Controller(events = [EventType.MESSAGE, EventType.QUICK_REPLY], pattern = "(?i)start.*")
    fun onAskForStart(event: Event) {
        val senderId = event.sender.id
        val map = appData.activeUserMap
        if (map.containsKey(senderId)) {
            reply(event, SESSION_EXISTS)
        }
        map[senderId] = DateTime.now()
        reply(event, QuickMessage(START_MESSAGE))
        reply(event, QuickMessage(appData.currentMessage))
    }

    @Controller(events = [EventType.MESSAGE, EventType.QUICK_REPLY], pattern = "(?i)stop.*")
    fun onAskForStop(event: Event) {
        appData.activeUserMap.remove(event.sender.id)
        reply(event, QuickMessage(STOP_MESSAGE))
    }

    @Controller(events = [EventType.MESSAGE, EventType.QUICK_REPLY])
    fun onReceiveMessage(event: Event) {
        reply(event, QuickMessage(UNKNOWN_MESSAGE))
    }

    /**
     * Used to send a plaintext message to a user given an application specific ID.
     */
    fun sendMessageToUser(userId: String, text: String) {
        val event = Event()
        val message = QuickMessage(text)
        event.recipient = User().setId(userId)
        event.message = message
        reply(event)
    }
}
