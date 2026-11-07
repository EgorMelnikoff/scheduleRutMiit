package com.egormelnikoff.schedulerutmiit.core.ui.widget.bottom_nav

import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route

sealed interface AppNavBarAction {
    data class OnNavigatePage(val page: Route.Page) : AppNavBarAction
}