package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import java.time.LocalDate

sealed interface ScheduleAction {
    data object OnP2R : ScheduleAction
    data class OnViewClicked(
        val view: ScheduleView
    ) : ScheduleAction

    data object OnMenuVertClicked : ScheduleAction
    data object OnTitleClicked : ScheduleAction
    data object OnDeleteClicked : ScheduleAction
    data object OnDeleteSchedule : ScheduleAction
    data object OnRenameClicked : ScheduleAction
    data object OnSavePdfClicked : ScheduleAction

    data class OnEditEventClicked(
        val eventId: Long?,
        val scheduleId: Long
    ) : ScheduleAction

    data object OnHiddenEventsClicked : ScheduleAction
    data class OnChangeSchedule(
        val id: Long
    ) : ScheduleAction

    data class OnEventClicked(
        val id: Long,
        val date: LocalDate
    ) : ScheduleAction

    data class OnHideEventClicked(
        val id: Long
    ) : ScheduleAction

    data class OnHideEvent(
        val id: Long
    ) : ScheduleAction

    data class OnDeleteEventClicked(
        val id: Long
    ) : ScheduleAction

    data class OnDeleteEvent(
        val id: Long
    ) : ScheduleAction
}