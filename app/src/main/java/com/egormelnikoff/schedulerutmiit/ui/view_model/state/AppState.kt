package com.egormelnikoff.schedulerutmiit.ui.view_model.state

data class AppState(
    val updatesAvailable: Boolean = false,
    val isUpdating: Boolean = false
)