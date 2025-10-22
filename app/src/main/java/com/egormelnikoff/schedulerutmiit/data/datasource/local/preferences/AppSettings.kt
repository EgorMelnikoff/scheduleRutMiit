package com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences

data class AppSettings(
    val theme: String,
    val decorColorIndex: Int,
    val eventView: Boolean,
    val calendarView: Boolean,
    val showCountClasses: Boolean,
)