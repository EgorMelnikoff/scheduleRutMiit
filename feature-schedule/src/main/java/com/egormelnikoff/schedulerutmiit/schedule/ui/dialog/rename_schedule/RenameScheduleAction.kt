package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule

sealed interface RenameScheduleAction {
    data object OnBackClicked : RenameScheduleAction
    data class OnNameChanged(
        val name: String
    ) : RenameScheduleAction

    data object OnSaveClicked : RenameScheduleAction
}