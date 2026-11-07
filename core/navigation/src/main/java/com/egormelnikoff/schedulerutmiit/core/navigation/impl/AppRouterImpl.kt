package com.egormelnikoff.schedulerutmiit.core.navigation.impl

import androidx.compose.runtime.mutableStateListOf
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import javax.inject.Inject

class AppRouterImpl @Inject constructor(): AppRouter {
    override val pageBackStack = mutableStateListOf<Route.Page>(Route.Page.Schedule)

    override val dialogBackStack = mutableStateListOf<Route.Dialog>(Route.Dialog.Empty)

    override fun openPage(page: Route.Page) {
        when {
            pageBackStack.size == 1 -> pageBackStack.add(page)
            page is Route.Page.Schedule -> pageBackStack.removeAt(pageBackStack.lastIndex)
            else -> pageBackStack[pageBackStack.lastIndex] = page
        }
    }

    override fun navigateToStartRage() {
        if (lastPage() !is Route.Page.Schedule) {
            openPage(Route.Page.Schedule)
        } else {
            pageBackStack[pageBackStack.lastIndex] = Route.Page.Schedule
        }
    }

    override fun openDialog(dialog: Route.Dialog) {
        dialogBackStack.add(dialog)
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
}