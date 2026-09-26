package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events

sealed interface HiddenEventsAction {
    data object OnBackClicked : HiddenEventsAction
    data class OnShowEvent(val id: Long) : HiddenEventsAction
}