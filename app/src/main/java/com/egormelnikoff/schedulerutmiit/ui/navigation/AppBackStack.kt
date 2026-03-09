package com.egormelnikoff.schedulerutmiit.ui.navigation

import androidx.compose.runtime.mutableStateListOf

class AppBackStack(
    private val startRage: Route.Page
) {
    val pageBackStack = mutableStateListOf(startRage)

    val dialogBackStack = mutableStateListOf<Route.Dialog>(Route.Dialog.Empty)

    fun openPage(page: Route.Page) {
        when {
            pageBackStack.size == 1 -> pageBackStack.add(page)
            page == startRage -> pageBackStack.removeAt(pageBackStack.lastIndex)
            else -> pageBackStack[pageBackStack.lastIndex] = page
        }
    }

    fun navigateToStartRage() {
        if (lastPage() != startRage) {
            openPage(startRage)
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