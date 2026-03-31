package com.egormelnikoff.schedulerutmiit.app.enums

import androidx.annotation.Keep

@Keep
enum class ScheduleView {
    CALENDAR, LIST, SPLIT_WEEKS;

    fun next(
        isPeriodic: Boolean
    ): ScheduleView {
        return when (this) {
            CALENDAR ->
                if (isPeriodic) SPLIT_WEEKS
                else LIST

            SPLIT_WEEKS -> LIST

            LIST -> CALENDAR
        }
    }
}