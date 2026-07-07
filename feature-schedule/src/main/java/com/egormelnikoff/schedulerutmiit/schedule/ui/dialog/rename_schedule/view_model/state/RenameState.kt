package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.view_model.state

sealed interface RenameState {
    data object Loading : RenameState

    data class Loaded(
        val currentName: String,
        val newName: String
    ) : RenameState {
        val renameEnabled: Boolean
            get() = newName.isNotBlank() &&
                    newName.trim() != currentName
    }
}