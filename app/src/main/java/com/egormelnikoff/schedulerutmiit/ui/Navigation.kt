package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.runtime.mutableStateListOf
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData

sealed class Routes(val isDialog: Boolean) {
    data object Search : Routes(false)
    data object Schedule : Routes(false)
    data object NewsList : Routes(false)
    data object Settings : Routes(false)

    data class EventDialog(
        val event: Event,
        val eventExtraData: EventExtraData?
    ) : Routes(true)

    data object News : Routes(true)
    data object Schedules : Routes(true)
    data object Info : Routes(true)
}

class AppBackStack<T : Routes>(
    startRoute: T
) {
    val backStack = mutableStateListOf<Routes>(startRoute)

    fun last() = backStack.last()

    fun onBack() {
        backStack.removeAt(backStack.lastIndex)
    }

    fun navigateToPage(route: Routes) {
        if (backStack.last().isDialog) {
            backStack.removeAt(backStack.lastIndex)
        }
        backStack.removeAt(0)
        backStack.add(route)
    }

    fun navigateToDialog(dialog: Routes) {
        backStack.add(dialog)
    }
}