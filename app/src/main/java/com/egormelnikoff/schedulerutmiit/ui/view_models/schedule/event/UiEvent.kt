package com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.event

sealed interface UiEvent {
    data class ErrorMessage(val message: String) : UiEvent
    data class InfoMessage(val message: String) : UiEvent
}