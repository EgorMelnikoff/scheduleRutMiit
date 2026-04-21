package com.egormelnikoff.schedulerutmiit.data.local.preferences

import com.egormelnikoff.schedulerutmiit.app.enums.Theme

data class DecorPreferences(
    val theme: Theme,
    val usedAmoled: Boolean,
    val decorColorIndex: Int,
)
