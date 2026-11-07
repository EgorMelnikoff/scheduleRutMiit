package com.egormelnikoff.schedulerutmiit.core.ui.widget.bottom_nav

import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.Theme

data class AppNavBarState(
    val theme: Theme = Theme.SYSTEM,
    val currentPage: Route.Page = Route.Page.Schedule,
    val updatesAvailable: Boolean = false
)