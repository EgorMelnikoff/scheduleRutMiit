package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.rename_schedule

sealed interface RenameScheduleAction {
    data object OnBackClicked : RenameScheduleAction
    data class OnNameChanged(
        val name: String
    ) : RenameScheduleAction

    data object OnSaveClicked : RenameScheduleAction
}