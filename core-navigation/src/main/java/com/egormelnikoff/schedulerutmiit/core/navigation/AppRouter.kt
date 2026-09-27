package com.egormelnikoff.schedulerutmiit.core.navigation

import androidx.compose.runtime.mutableStateListOf
import com.egormelnikoff.schedulerutmiit.core.common.domain.NavigateSchedule

class AppRouter {
    val pageBackStack = mutableStateListOf<Route.Page>(Route.Page.Schedule())

    val dialogBackStack = mutableStateListOf<Route.Dialog>(Route.Dialog.Empty)

    fun openPage(page: Route.Page) {
        when {
            pageBackStack.size == 1 -> pageBackStack.add(page)
            page is Route.Page.Schedule -> pageBackStack.removeAt(pageBackStack.lastIndex)
            else -> pageBackStack[pageBackStack.lastIndex] = page
        }
    }

    fun navigateToStartRage(navigateSchedule: NavigateSchedule? = null) {
        if (lastPage() !is Route.Page.Schedule) {
            openPage(Route.Page.Schedule(navigateSchedule))
        } else {
            pageBackStack[pageBackStack.lastIndex] = Route.Page.Schedule(
                navigateSchedule
            )
        }
    }

    fun openDialog(dialog: Route.Dialog) {
        dialogBackStack.add(dialog)
    }

    fun onBack() {
        if (dialogBackStack.size > 1) {
            dialogBackStack.removeAt(dialogBackStack.lastIndex)
            return
        }

        if (pageBackStack.size > 1) {
            pageBackStack.removeAt(pageBackStack.lastIndex)
        }
    }

    fun lastPage(): Route.Page = pageBackStack.last()
}