package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event

import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule

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