package com.egormelnikoff.schedulerutmiit.schedule.view_model.event

sealed interface UiEvent {
    data class ErrorMessage(val message: String) : UiEvent
    data class InfoMessage(val message: String) : UiEvent
}