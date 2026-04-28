package com.egormelnikoff.schedulerutmiit.core.common.enums

enum class ScheduleView {
    CALENDAR, LIST;

    fun next() = when (this) {
        CALENDAR -> LIST
        LIST -> CALENDAR
    }
}