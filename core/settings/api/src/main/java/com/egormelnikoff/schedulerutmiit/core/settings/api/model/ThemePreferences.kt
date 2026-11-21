package com.egormelnikoff.schedulerutmiit.core.settings.api.model

data class ThemePreferences(
    val theme: Theme,
    val usedAmoled: Boolean,
    val decorColorIndex: Int,
)