package com.egormelnikoff.schedulerutmiit.ui.screens.schedule


enum class ScheduleView{
    CALENDAR, LIST, SPLIT_WEEKS
}

fun ScheduleView.next(
    isPeriodic: Boolean
): ScheduleView {
    return when (this) {
        ScheduleView.CALENDAR -> {
            if (isPeriodic) ScheduleView.SPLIT_WEEKS
            else ScheduleView.LIST
        }

        ScheduleView.SPLIT_WEEKS -> {
            ScheduleView.LIST
        }

        ScheduleView.LIST -> {
            ScheduleView.CALENDAR
        }
    }
}