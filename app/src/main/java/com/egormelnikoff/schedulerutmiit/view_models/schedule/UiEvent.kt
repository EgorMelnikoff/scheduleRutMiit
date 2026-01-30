package com.egormelnikoff.schedulerutmiit.view_models.schedule

sealed interface UiEvent {
    data class ErrorMessage(val message: String) : UiEvent
    data class InfoMessage(val message: String) : UiEvent
}