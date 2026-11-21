package com.egormelnikoff.schedulerutmiit.core.settings.ui

import com.egormelnikoff.schedulerutmiit.core.settings.api.model.AppSettings

data class SettingsState(
    val updatesAvailable: Boolean = false,
    val activeDialog: SettingsDialog? = null,
    val importDialog: Boolean = false,
    val appSettings: AppSettings? = null
)