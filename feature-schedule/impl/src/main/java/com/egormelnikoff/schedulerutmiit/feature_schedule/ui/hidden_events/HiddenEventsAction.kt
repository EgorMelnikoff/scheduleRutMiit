package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.hidden_events

sealed interface HiddenEventsAction {
    data object OnBackClicked : HiddenEventsAction
    data class OnShowEvent(val id: Long) : HiddenEventsAction
}