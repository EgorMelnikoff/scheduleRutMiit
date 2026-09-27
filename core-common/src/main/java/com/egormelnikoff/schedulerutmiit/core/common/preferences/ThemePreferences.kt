package com.egormelnikoff.schedulerutmiit.core.common.preferences

import com.egormelnikoff.schedulerutmiit.core.common.domain.enums.Theme

data class ThemePreferences(
    val theme: Theme,
    val usedAmoled: Boolean,
    val decorColorIndex: Int,
)