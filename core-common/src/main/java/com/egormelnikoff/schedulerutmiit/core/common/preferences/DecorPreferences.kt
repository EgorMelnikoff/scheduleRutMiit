package com.egormelnikoff.schedulerutmiit.core.common.preferences

import com.egormelnikoff.schedulerutmiit.core.common.enums.Theme

data class DecorPreferences(
    val theme: Theme,
    val usedAmoled: Boolean,
    val decorColorIndex: Int,
)
