package com.egormelnikoff.schedulerutmiit.core.settings.api.model

enum class ScheduleView {
    CALENDAR, LIST;

    fun next() = when (this) {
        CALENDAR -> LIST
        LIST -> CALENDAR
    }
}