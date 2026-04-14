package com.egormelnikoff.schedulerutmiit.view_models.settings.state

data class SettingsState(
    val updatesAvailable: Boolean = false,
    val isUpdating: Boolean = false
)