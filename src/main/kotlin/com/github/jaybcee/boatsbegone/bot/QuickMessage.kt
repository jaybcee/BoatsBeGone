package com.github.jaybcee.boatsbegone.bot

import me.ramswaroop.jbot.core.facebook.models.Button
import me.ramswaroop.jbot.core.facebook.models.Message

class QuickMessage(text: String) : Message() {
    init {
        super.setText(text)
        val quickReplies = arrayOf(
            Button().setContentType("text").setTitle("Status ⛵").setPayload("status"),
            Button().setContentType("text").setTitle("Start \uD83C\uDFC1").setPayload("start"),
            Button().setContentType("text").setTitle("Stop \uD83D\uDED1").setPayload("stop")
        )
        super.setQuickReplies(quickReplies)
    }
}
