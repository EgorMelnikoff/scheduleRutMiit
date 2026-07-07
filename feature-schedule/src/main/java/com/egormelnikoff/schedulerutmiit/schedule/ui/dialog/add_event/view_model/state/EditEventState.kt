package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule

sealed interface EditEventState {
    data object Loading : EditEventState

    data class Loaded(
        val schedule: Schedule
    ) : EditEventState
}