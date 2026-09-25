package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule

sealed interface EventScreenState {
    data object Loading : EventScreenState

    data class Loaded(
        val event: Event,
        val schedule: Schedule,
        val comment: String,
        val tag: Int,

        val showDeleteDialog: Boolean = false,
        val showHideDialog: Boolean = false,
        val showActionsDialog: Boolean = false
    ) : EventScreenState {
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