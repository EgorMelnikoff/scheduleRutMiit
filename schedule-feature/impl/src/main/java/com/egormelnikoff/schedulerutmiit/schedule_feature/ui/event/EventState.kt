package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.event

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Schedule

sealed interface EventState {
    data object Loading : EventState

    data class Loaded(
        val event: Event,
        val schedule: Schedule,
        val comment: String,
        val tag: Int,

        val showDeleteDialog: Boolean = false,
        val showHideDialog: Boolean = false,
        val showActionsDialog: Boolean = false
    ) : EventState {
        fun showDeleteDialog(showDeleteDialog: Boolean) = copy(
            showDeleteDialog = showDeleteDialog
        )

        fun showHideDialog(showHideDialog: Boolean) = copy(
            showHideDialog = showHideDialog
        )

        fun showActionsDialog(showActionsDialog: Boolean) = copy(
            showActionsDialog = showActionsDialog
        )
    }
}