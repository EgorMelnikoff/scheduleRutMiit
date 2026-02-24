package com.egormelnikoff.schedulerutmiit.app.enums

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