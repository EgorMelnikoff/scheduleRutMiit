package com.egormelnikoff.schedulerutmiit.core.navigation.impl

import androidx.compose.runtime.mutableStateListOf
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import javax.inject.Inject

class AppRouterImpl @Inject constructor() : AppRouter {
    override val pageBackStack = mutableStateListOf<Route.Page>(Route.Page.Schedule)

    override val dialogBackStack = mutableStateListOf<Route.Dialog>(Route.Dialog.Empty)

    override fun navigateTo(
        route: Route,
        back: Boolean
    ) {
        when (route) {
            is Route.Page -> openPage(route)
            is Route.Dialog -> {
                dialogBackStack.add(route)
            }
        }

        if (back) onBack()
    }

    override fun onBack() {
        if (dialogBackStack.size > 1) {
            dialogBackStack.removeAt(dialogBackStack.lastIndex)
            return
        }

        if (pageBackStack.size > 1) {
            pageBackStack.removeAt(pageBackStack.lastIndex)
        }
    }

    override fun lastPage() = pageBackStack.last()

    private fun openPage(page: Route.Page) {
        when {
            page is Route.Page.Schedule -> if (pageBackStack.size > 1) {
                pageBackStack.removeAt(pageBackStack.lastIndex)
            }
            pageBackStack.size == 1 -> pageBackStack.add(page)
            else -> pageBackStack[pageBackStack.lastIndex] = page
        }
    }
}