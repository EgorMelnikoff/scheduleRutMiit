package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule

sealed interface AddEventState {
    data object Loading : AddEventState

    data class Loaded(
        val schedule: Schedule,
        val editForm: EditForm,

        val showDialogDate: Boolean = false,
        val showDialogStartTime: Boolean = false,
        val showDialogEndTime: Boolean = false,
        val showBackDialog: Boolean = false
    ) : AddEventState
}