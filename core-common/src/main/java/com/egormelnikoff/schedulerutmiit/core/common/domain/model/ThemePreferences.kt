package com.egormelnikoff.schedulerutmiit.core.common.domain.model

import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.Theme

data class ThemePreferences(
    val theme: Theme,
    val usedAmoled: Boolean,
    val decorColorIndex: Int,
)