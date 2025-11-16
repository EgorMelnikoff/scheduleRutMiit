package com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences

data class AppSettings(
    val theme: String,
    val decorColorIndex: Int,
    val calendarView: Boolean,
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