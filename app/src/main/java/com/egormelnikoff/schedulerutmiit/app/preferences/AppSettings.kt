package com.egormelnikoff.schedulerutmiit.app.preferences

import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.enums.Theme

data class AppSettings(
    val decorPreferences: DecorPreferences,
    val scheduleView: ScheduleView,
    val eventView: EventView,
    val eventsCountView: EventsCountView,
    val schedulesDeletable: Boolean,
    val syncTagsAndComments: Boolean,
    val skipWelcomePage: Boolean
)

data class DecorPreferences(
    val theme: Theme,
    val usedAmoled: Boolean,
    val decorColorIndex: Int,
)

data class EventView(
    val groupsVisible: Boolean,
    val roomsVisible: Boolean,
    val lecturersVisible: Boolean,
    val tagVisible: Boolean,
    val commentVisible: Boolean
)