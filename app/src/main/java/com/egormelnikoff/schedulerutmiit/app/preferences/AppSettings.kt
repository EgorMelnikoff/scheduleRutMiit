package com.egormelnikoff.schedulerutmiit.app.preferences

import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.enums.Theme

data class AppSettings(
    val theme: Theme,
    val usedAmoled: Boolean,
    val decorColorIndex: Int,
    val scheduleView: ScheduleView,
    val eventsCountView: EventsCountView,
    val schedulesDeletable: Boolean,
    val syncTagsAndComments: Boolean,
    val eventView: EventView
)

data class EventView(
    val groupsVisible: Boolean,
    val roomsVisible: Boolean,
    val lecturersVisible: Boolean,
    val tagVisible: Boolean,
    val commentVisible: Boolean
)