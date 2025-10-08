package com.egormelnikoff.schedulerutmiit.ui.navigation

import androidx.compose.runtime.mutableStateListOf

class AppBackStack<T : Routes>(
    startRoute: T
) {
    val backStack = mutableStateListOf<Routes>(startRoute)

    fun last() = backStack.last()

    fun lastPage() = backStack.last { !it.isDialog }

    fun onBack() {
        backStack.removeAt(backStack.lastIndex)
    }

    fun navigateToPage(route: Routes) {
        if (last().isDialog) {
            onBack()
        }
        backStack.removeAt(0)
        backStack.add(route)
    }

    fun navigateToDialog(dialog: Routes) {
        backStack.add(dialog)
    }
}