package com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.event

sealed interface UiEvent {
    data class ErrorMessage(val message: String, val useSnackBar: Boolean = true) : UiEvent
    data class InfoMessage(val message: String, val useSnackBar: Boolean = true) : UiEvent
}