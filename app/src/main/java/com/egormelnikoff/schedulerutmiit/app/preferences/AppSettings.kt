package com.egormelnikoff.schedulerutmiit.app.preferences

import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView

data class AppSettings(
    val theme: String,
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