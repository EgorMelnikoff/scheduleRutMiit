package com.egormelnikoff.schedulerutmiit.ui.navigation

import androidx.compose.runtime.mutableStateListOf

class AppBackStack<T : Route.Page>(
    private val startRoute: T
) {
    val pageBackStack = mutableStateListOf<Route.Page>(startRoute)

    val dialogBackStack = mutableStateListOf<Route.Dialog>(Route.Dialog.Empty)

    fun openPage(page: Route.Page) {
        when {
            pageBackStack.size == 1 -> pageBackStack.add(page)
            page == startRoute -> pageBackStack.removeAt(pageBackStack.lastIndex)
            else -> pageBackStack[pageBackStack.lastIndex] = page
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