package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.rename_schedule

sealed interface RenameScheduleState {
    data object Loading : RenameScheduleState

    data class Loaded(
        val currentName: String,
        val newName: String
    ) : RenameScheduleState {
        val renameEnabled: Boolean
            get() = newName.isNotBlank() &&
                    newName.trim() != currentName
    }
}