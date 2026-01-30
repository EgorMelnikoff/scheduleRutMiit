package com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences

data class AppSettings(
    val theme: String,
    val decorColorIndex: Int,
    val scheduleView: Boolean,
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