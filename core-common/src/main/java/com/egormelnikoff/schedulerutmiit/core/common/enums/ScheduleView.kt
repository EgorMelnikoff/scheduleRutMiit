package com.egormelnikoff.schedulerutmiit.core.common.enums

import androidx.annotation.Keep

@Keep
enum class ScheduleView {
    CALENDAR, LIST;

    fun next() = when (this) {
        CALENDAR -> LIST
        LIST -> CALENDAR
    }
}