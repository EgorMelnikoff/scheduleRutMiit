package com.egormelnikoff.schedulerutmiit.app.enums

import androidx.annotation.Keep

@Keep
enum class ScheduleView {
    CALENDAR, LIST;

    fun next() = when (this) {
        CALENDAR -> LIST
        LIST -> CALENDAR
    }
}