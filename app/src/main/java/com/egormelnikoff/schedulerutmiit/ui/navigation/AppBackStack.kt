package com.egormelnikoff.schedulerutmiit.ui.navigation

import androidx.compose.runtime.mutableStateListOf

class AppBackStack<T : Route>(
    startRoute: T
) {
    val backStack = mutableStateListOf<Route>(startRoute)

    fun last() = backStack.last()

    fun lastPage() = backStack.last { it !is Route.Dialog }

    fun onBack() {
        backStack.removeAt(backStack.lastIndex)
    }

    fun navigateToPage(page: Route) {
        backStack.removeIf { it is Route.Dialog }
        backStack[backStack.lastIndex] = page
    }

    fun navigateToDialog(dialog: Route) {
        backStack.add(dialog)
    }
}