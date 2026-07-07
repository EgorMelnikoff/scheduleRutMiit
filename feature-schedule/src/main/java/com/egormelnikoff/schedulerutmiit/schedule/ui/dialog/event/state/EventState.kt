package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.state

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule

sealed interface EventState {
    data object Loading : EventState

    data class Loaded(
        val event: Event,
        val schedule: Schedule,
        val comment: String,
        val tag: Int
    ) : EventState
}