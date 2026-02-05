package com.egormelnikoff.schedulerutmiit.app.preferences

import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.ScheduleView

data class AppSettings(
    val theme: String,
    val decorColorIndex: Int,
    val scheduleView: ScheduleView,
    val schedulesDeletable: Boolean,
    val showCountClasses: Boolean,
    val eventView: EventView
)

data class EventView(
    val groupsVisible: Boolean,
    val roomsVisible: Boolean,
    val lecturersVisible: Boolean,
    val tagVisible: Boolean,
    val commentVisible: Boolean
)