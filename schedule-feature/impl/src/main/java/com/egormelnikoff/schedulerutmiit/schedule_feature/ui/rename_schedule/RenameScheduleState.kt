package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.rename_schedule

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