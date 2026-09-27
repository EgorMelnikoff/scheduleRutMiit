package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule

import com.egormelnikoff.schedulerutmiit.core.common.domain.enums.TimetableType
import java.time.LocalDate

sealed interface AddScheduleAction {
    data class OnUpdateName(
        val name: String
    ) : AddScheduleAction

    data class OnUpdateType(
        val timetableType: TimetableType
    ) : AddScheduleAction

    data class OnUpdateDates(
        val startDate: LocalDate,
        val endDate: LocalDate
    ) : AddScheduleAction

    data object OnBackClicked : AddScheduleAction

    data object OnSaveClicked : AddScheduleAction

    data class OnShowBackDialog(val show: Boolean) : AddScheduleAction

    data class OnShowDialogDate(val show: Boolean) : AddScheduleAction
}