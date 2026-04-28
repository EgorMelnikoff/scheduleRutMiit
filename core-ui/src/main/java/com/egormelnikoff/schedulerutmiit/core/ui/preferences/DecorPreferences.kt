package com.egormelnikoff.schedulerutmiit.core.ui.preferences

import com.egormelnikoff.schedulerutmiit.core.common.enums.Theme

data class DecorPreferences(
    val theme: Theme,
    val usedAmoled: Boolean,
    val decorColorIndex: Int,
)
