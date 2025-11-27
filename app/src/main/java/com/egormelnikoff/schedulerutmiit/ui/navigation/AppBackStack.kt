package com.egormelnikoff.schedulerutmiit.ui.navigation

import androidx.compose.runtime.mutableStateListOf

class AppBackStack<T : Route>(
    startRoute: T
) {
    val backStack = mutableStateListOf<Route>(startRoute)

    fun last() = backStack.last()

    fun lastPage(): Route.Page {
        return backStack.last { it is Route.Page } as Route.Page
    }

    fun onBack() {
        backStack.removeAt(backStack.lastIndex)
    }

    fun navigateToPage(page: Route.Page) {
        backStack.removeIf { it is Route.Dialog }
        backStack[backStack.lastIndex] = page
    }

    fun navigateToDialog(dialog: Route.Dialog) {
        backStack.add(dialog)
    }
}