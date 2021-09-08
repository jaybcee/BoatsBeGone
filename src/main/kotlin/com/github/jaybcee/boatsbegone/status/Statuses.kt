package com.github.jaybcee.boatsbegone.status

object Statuses {
    const val AVAILABLE_MESSAGE = "The bridge is now AVAILABLE. It has been available since %s. Happy biking! 🚴"
    const val UNAVAILABLE_SOON = "The bridge is RAISING SOON. Get going!"
    const val UNAVAILABLE_MESSAGE = "The bridge is UNAVAILABLE. It has been unavailable since %s."
    const val EXPIRED_TIME = "Time is up! You have been removed after 2 hours."
    const val START_MESSAGE =
        "Hello! You have been added to the notification list. You will receive realtime updates for the next 2 hours."
    const val SESSION_EXISTS = "Looks like you are already registered! Extending your session for another 2 hours."
    const val STOP_MESSAGE = "You have been removed from the list! You will stop receiving notifications shortly."
    const val UNKNOWN_MESSAGE = "Hmm... Not sure what to do about that. Chose an option below."
    const val HOURS_AND_MINUTES = "HH:mm"
}
