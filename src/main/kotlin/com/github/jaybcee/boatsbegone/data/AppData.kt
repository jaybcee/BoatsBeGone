package com.github.jaybcee.boatsbegone.data

import org.springframework.stereotype.Component
import org.joda.time.DateTime;

/**
 * A Spring component that holds data used across the application.
 * Ideally, some of this should be done in a proper database.
 * The use of a map is done because we want to remove users who have stale sessions,
 * however, we still require a Set like uniqueness for correctness.
 */
@Component("appData")
class AppData {
    val activeUserMap = mutableMapOf<String, DateTime>()
    final var timeOfLastChange: DateTime = DateTime.now()
    var currentMessage = String.format(
        "The bridge is now AVAILABLE. It has been available since %s:%s. Happy biking! 🚴",
        timeOfLastChange.hourOfDay,
        timeOfLastChange.minuteOfHour
    )
}


