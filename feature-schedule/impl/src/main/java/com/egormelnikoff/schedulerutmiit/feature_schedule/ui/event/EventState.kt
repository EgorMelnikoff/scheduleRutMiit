package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule

internal sealed interface EventState {
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
        fun showDeleteDialog(showDeleteDialog: Boolean) = if (showDeleteDialog) copy(
            showDeleteDialog = true,
            showHideDialog = false,
            showActionsDialog = false
        ) else copy(showDeleteDialog = false)

        fun showHideDialog(showHideDialog: Boolean) = if (showHideDialog) copy(
            showHideDialog = true,
            showDeleteDialog = false,
            showActionsDialog = false
        ) else copy(showHideDialog = false)

        fun showActionsDialog(showActionsDialog: Boolean) = if (showActionsDialog) copy(
            showActionsDialog = true,
            showDeleteDialog = false,
            showHideDialog = false
        ) else copy(showActionsDialog = false)
    }
}