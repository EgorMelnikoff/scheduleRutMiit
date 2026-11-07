package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule

sealed interface ReviewAction {
    data object OnFindScheduleClicked : ReviewAction
    data object OnCreateScheduleClicked : ReviewAction
    data object OnNewsCLicked : ReviewAction
    data object OnCurriculumClicked : ReviewAction
    data class OnRenameScheduleClicked(
        val namedScheduleId: Long
    ) : ReviewAction

    data class OnShowSchedules(val show: Boolean) : ReviewAction
    data class OnShowServices(val show: Boolean) : ReviewAction

    data class OnSetNamedSchedule(
        val namedScheduleId: Long,
        val setDefault: Boolean
    ) : ReviewAction

    data class OnDeleteNamedSchedule(val id: Long, val isDefault: Boolean) : ReviewAction

    data class OnShowScheduleSheet(val namedSchedule: NamedSchedule?) : ReviewAction
    data class OnShowDeleteScheduleSheet(val namedSchedule: NamedSchedule?) : ReviewAction
}