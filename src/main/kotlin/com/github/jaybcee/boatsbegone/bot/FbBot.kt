package com.github.jaybcee.boatsbegone.bot

import com.github.jaybcee.boatsbegone.data.AppData
import me.ramswaroop.jbot.core.common.Controller
import me.ramswaroop.jbot.core.common.EventType
import me.ramswaroop.jbot.core.common.JBot
import me.ramswaroop.jbot.core.facebook.Bot
import me.ramswaroop.jbot.core.facebook.models.Event
import me.ramswaroop.jbot.core.facebook.models.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import java.util.*

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
        val text = "Hello! You have been added to the notification list. " +
                "You will receive realtime updates for the next 2 hours."
        val senderId = event.sender.id
        val map = appData.activeUserMap
        if (map.containsKey(senderId)) {
            reply(event, "Looks like you are already registered! Extending your session for another 2 hours.")
        }
        map[senderId] = Calendar.getInstance()
        reply(event, QuickMessage(text))
        reply(event, QuickMessage(appData.currentMessage))
    }

    @Controller(events = [EventType.MESSAGE, EventType.QUICK_REPLY], pattern = "(?i)stop.*")
    fun onAskForStop(event: Event) {
        appData.activeUserMap.remove(event.sender.id)
        reply(
            event, QuickMessage(
                "You have been removed from the list! You will stop receiving notifications shortly."
            )
        )
    }

    @Controller(events = [EventType.MESSAGE, EventType.QUICK_REPLY])
    fun onReceiveMessage(event: Event) {
        reply(event, QuickMessage("Hmm... Not sure what to do about that. Chose an option below."))
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