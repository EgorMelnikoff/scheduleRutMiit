package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.rename_schedule

sealed interface RenameScheduleAction {
    data object OnBackClicked : RenameScheduleAction
    data class OnNameChanged(
        val name: String
    ) : RenameScheduleAction

    data object OnSaveClicked : RenameScheduleAction
}