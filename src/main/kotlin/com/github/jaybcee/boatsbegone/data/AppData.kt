package com.github.jaybcee.boatsbegone.data

import com.github.jaybcee.boatsbegone.status.Statuses.AVAILABLE_MESSAGE
import com.github.jaybcee.boatsbegone.status.Statuses.HOURS_AND_MINUTES
import org.joda.time.DateTime
import org.springframework.stereotype.Component

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
    var currentMessage = String.format(AVAILABLE_MESSAGE, timeOfLastChange.toString(HOURS_AND_MINUTES))
}
